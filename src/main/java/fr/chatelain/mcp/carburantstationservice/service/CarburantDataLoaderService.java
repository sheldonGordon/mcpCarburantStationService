package fr.chatelain.mcp.carburantstationservice.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.chatelain.mcp.carburantstationservice.mapper.CarburantDataMapper;
import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import fr.chatelain.mcp.carburantstationservice.model.dto.CarburantJsonDTO;
import fr.chatelain.mcp.carburantstationservice.repository.StationCarburantRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service pour charger et traiter les données de carburants depuis l'API gouvernementale
 */
@Slf4j
@Service
public class CarburantDataLoaderService {

    private static final String DATA_API_URL = "https://data.economie.gouv.fr/api/explore/v2.1/catalog/datasets/prix-carburants-quotidien/exports/json";
    private static final int BATCH_SIZE = 500; // Traiter par batch pour éviter les problèmes mémoire
    private static final int CONNECTION_TIMEOUT = 60000; // 60 secondes
    private static final int READ_TIMEOUT = 60000; // 60 secondes

    @Autowired
    private StationCarburantRepository stationRepository;

    @Autowired
    private CarburantDataMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

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
        
        URL url = URI.create(DATA_API_URL).toURL();
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        try {
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");
            
            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new IOException("Erreur HTTP: " + responseCode);
            }

            // Parser le JSON avec streaming pour économiser la mémoire
            try (BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                
                // Lire le contenu entier (nécessaire car Jackson ne supporte pas bien le streaming en liste)
                String jsonContent = reader.lines().collect(Collectors.joining());
                
                // Parser le JSON
                List<CarburantJsonDTO> data = objectMapper.readValue(
                        jsonContent,
                        new TypeReference<List<CarburantJsonDTO>>() {}
                );
                
                log.info("JSON parsé: {} objets trouvés", data.size());
                return data;
            }

        } finally {
            connection.disconnect();
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
                Long stationId = Long.parseLong(dto.getId());
                
                Optional<StationCarburant> existing = stationRepository.findById(stationId);
                
                if (existing.isPresent()) {
                    // Mettre à jour
                    StationCarburant updated = mapper.mapToStationCarburant(dto);
                    if (updated != null) {
                        stationRepository.save(updated);
                        stats.put("updated", stats.get("updated") + 1);
                    }
                } else {
                    // Créer
                    StationCarburant newStation = mapper.mapToStationCarburant(dto);
                    if (newStation != null) {
                        stationRepository.save(newStation);
                        stats.put("created", stats.get("created") + 1);
                    }
                }
            } catch (Exception e) {
                log.warn("Erreur lors du traitement de l'enregistrement {}: {}", dto.getId(), e.getMessage());
                stats.put("errors", stats.get("errors") + 1);
            }
        }

        return stats;
    }
}

