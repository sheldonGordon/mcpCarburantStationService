package fr.chatelain.mcp.carburantstationservice.service;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.chatelain.mcp.carburantstationservice.mapper.CarburantDataMapper;
import fr.chatelain.mcp.carburantstationservice.model.CarburantType;
import fr.chatelain.mcp.carburantstationservice.model.PrixCarburant;
import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import fr.chatelain.mcp.carburantstationservice.model.dto.carburant.CarburantJsonDTO;
import fr.chatelain.mcp.carburantstationservice.repository.PrixCarburantRepository;
import fr.chatelain.mcp.carburantstationservice.repository.StationCarburantRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.*;
import java.util.zip.GZIPInputStream;

/**
 * Service pour charger et traiter les données de carburants depuis l'API gouvernementale
 */
@Slf4j
@Service
@AllArgsConstructor
public class CarburantDataLoaderService {

    private static final String DATA_API_URL = "https://www.data.gouv.fr/api/1/datasets/r/04b8ffa6-1d5e-42c3-8687-0f27c0cc025a";
    private static final int BATCH_SIZE = 500; // Traiter par batch pour éviter les problèmes mémoire
    private static final int CONNECTION_TIMEOUT = 60000; // 60 secondes
    private static final int READ_TIMEOUT = 60000; // 60 secondes

    private StationCarburantRepository stationRepository;

    private PrixCarburantRepository prixCarburantRepository;

    private CarburantDataMapper mapper;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * Télécharge et traite les données de carburants
     */
    @Transactional
    public void loadCarburantData() {
        long startTime = System.currentTimeMillis();

        try {
            log.info("=== Début du téléchargement des données de carburants ===");
            log.info("URL: {}", DATA_API_URL);

            List<CarburantJsonDTO> allData = downloadJsonData();

            if (allData.isEmpty()) {
                log.warn("Aucune donnée téléchargée");
                return;
            }

            log.info("✓ {} enregistrements téléchargés", allData.size());

            // Traiter les données par batch
            processDataInBatches(allData);

            long duration = (System.currentTimeMillis() - startTime) / 1000;
            log.info("=== Chargement terminé en {} secondes ===", duration);

        } catch (Exception e) {
            log.error("Erreur lors du chargement des données de carburants", e);
            throw new RuntimeException("Erreur lors du chargement des données", e);
        }
    }

    /**
     * Télécharge le JSON depuis l'API
     */
    private List<CarburantJsonDTO> downloadJsonData() throws IOException {
        log.info("Téléchargement du fichier JSON (~120 Mo)...");

        final URL url = URI.create(DATA_API_URL).toURL();
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("GET");
            // On demande explicitement le GZIP
            connection.setRequestProperty("Accept-Encoding", "gzip");
            connection.setRequestProperty("Accept", "application/json");

            final int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Erreur HTTP: " + responseCode);
            }

            // Gestion automatique du GZIP via "Content-Encoding"
            final InputStream inputStream = "gzip".equals(connection.getContentEncoding())
                    ? new GZIPInputStream(connection.getInputStream())
                    : connection.getInputStream();

            final List<CarburantJsonDTO> data = traiterStream(inputStream);

            log.info("✓ Téléchargement terminé, {} enregistrements récupérés", data.size());
            return data;

        } finally {
            connection.disconnect();
        }
    }

    public List<CarburantJsonDTO> traiterStream(InputStream inputStream) throws IOException {
        final List<CarburantJsonDTO> data = new ArrayList<>();
        try (JsonParser parser = objectMapper.getFactory().createParser(inputStream)) {

            // Vérifie qu'on commence bien par un tableau "["
            if (parser.nextToken() != JsonToken.START_ARRAY) {
                throw new IllegalStateException("Le JSON attendu doit commencer par un tableau");
            }

            // Boucle sur chaque objet du tableau
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                // Maintenant, le parser utilise le codec de l'objectMapper
                JsonNode node = parser.readValueAsTree();
                CarburantJsonDTO dto = objectMapper.treeToValue(node, CarburantJsonDTO.class);
                data.add(dto);
            }
            return data;
        }
    }

    /**
     * Traite les données par batch pour éviter les problèmes mémoire
     */
    private void processDataInBatches(List<CarburantJsonDTO> allData) {
        int totalProcessed = 0;
        int totalUpdated = 0;
        int totalCreated = 0;
        int totalErrors = 0;

        for (int i = 0; i < allData.size(); i += BATCH_SIZE) {
            int endIndex = Math.min(i + BATCH_SIZE, allData.size());
            List<CarburantJsonDTO> batch = allData.subList(i, endIndex);

            log.info("Traitement du batch {}/{} ({} enregistrements)",
                    (i / BATCH_SIZE) + 1,
                    (allData.size() + BATCH_SIZE - 1) / BATCH_SIZE,
                    batch.size());

            Map<String, Integer> batchStats = processBatch(batch);

            totalProcessed += batch.size();
            totalUpdated += batchStats.getOrDefault("updated", 0);
            totalCreated += batchStats.getOrDefault("created", 0);
            totalErrors += batchStats.getOrDefault("errors", 0);

            // Forcer le garbage collection entre les batches
            System.gc();
        }

        log.info("=== Statistiques finales ===");
        log.info("Total traité: {}", totalProcessed);
        log.info("Créés: {}", totalCreated);
        log.info("Mis à jour: {}", totalUpdated);
        log.info("Erreurs: {}", totalErrors);
    }

    /**
     * Traite un batch d'enregistrements
     */
    private Map<String, Integer> processBatch(List<CarburantJsonDTO> batch) {
        Map<String, Integer> stats = new HashMap<>();
        stats.put("created", 0);
        stats.put("updated", 0);
        stats.put("errors", 0);

        for (CarburantJsonDTO dto : batch) {
            try {
                if (!StringUtils.hasLength(dto.id())) {
                    log.warn("ID manquant pour l'enregistrement: {}", dto);
                    stats.put("errors", stats.get("errors") + 1);
                    continue;
                }

                Long stationId = Long.parseLong(dto.id());

                Optional<StationCarburant> stationCarburant = stationRepository.findById(stationId);

                if (stationCarburant.isPresent()) {
                    // Créer ou mettre à jour le carburant uniquement
                    if(Objects.nonNull(dto.prixNom()) && Objects.nonNull(dto.prixValeur())) {
                        CarburantType.fromLabel(dto.prixNom())
                                .orElseThrow(() -> new IllegalArgumentException("Type de carburant non reconnu: " + dto.prixNom()));

                        StationCarburant station = stationCarburant.get();

                        PrixCarburant nouveauPrix = mapper.mapToPrixCarburant(dto, station);

                        if (Objects.nonNull(nouveauPrix)) {
                            boolean existeDeja = station.getPrixCarburants().stream()
                                    .anyMatch(p -> p.getCarburant().equals(nouveauPrix.getCarburant()));

                            station.upsertPrixCarburant(nouveauPrix);
                            stationRepository.save(station);

                            String key = existeDeja ? "updated" : "created";
                            stats.put(key, stats.get(key) + 1);
                        }
                    }
                } else {
                    // Créer
                    StationCarburant newStation = mapper.mapToStationCarburant(dto);
                    if (Objects.nonNull(newStation)) {
                        stationRepository.save(newStation);
                        stats.put("created", stats.get("created") + 1);
                    }
                }
            } catch (Exception e) {
                log.warn("Erreur lors du traitement de l'enregistrement {}: {}", dto.id(), e.getMessage());
                stats.put("errors", stats.get("errors") + 1);
            }
        }

        return stats;
    }
}
