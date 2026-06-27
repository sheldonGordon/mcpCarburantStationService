package fr.chatelain.mcp.carburantstationservice.tool;

import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import fr.chatelain.mcp.carburantstationservice.model.dto.adresse.CoordonneesDTO;
import fr.chatelain.mcp.carburantstationservice.repository.StationCarburantRepository;
import fr.chatelain.mcp.carburantstationservice.service.GeocodingService;
import lombok.AllArgsConstructor;
import org.springframework.ai.mcp.annotation.McpTool;
import org.springframework.ai.mcp.annotation.McpToolParam;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CarburantTools {

    private GeocodingService geocodingService;
    private StationCarburantRepository stationCarburantRepository;

    @McpTool(description = "Donne les stations services les proches dans un rayon de 1km")
    public String prixCarburantAutourAdresse(
            @McpToolParam(description = "Adresse à partir de laquelle chercher les stations services", required = true) String adresse){
        Mono<CoordonneesDTO> monoCoordonneesDTO = geocodingService.getCoordinates("ton adresse");

        // .blockOptional() va bloquer le thread courant jusqu'à ce que la réponse arrive
        Optional<CoordonneesDTO> OptCoordonneesDTO = monoCoordonneesDTO.blockOptional();

        if (OptCoordonneesDTO.isPresent()) {
            CoordonneesDTO coordonneesDTO = OptCoordonneesDTO.get();
            List<StationCarburant> stations = stationCarburantRepository.findStationsProches(new BigDecimal(coordonneesDTO.latitude()), new BigDecimal(coordonneesDTO.longitude()), 1.0);
            return formatStationsProches(stations);
        } else {
            return "Coordonnées introuvables pour l'adresse : " + adresse;
        }
    }

    @McpTool(description = "Donne les stations services les proches dans un rayon définit par l'utilisateur")
    public String prixCarburantAutourAdresseKilomtre(
            @McpToolParam(description = "Adresse à partir de laquelle chercher les stations services", required = true) String adresse,
            @McpToolParam(description = "Rayon de recherche en kilomètres", required = true) double rayonKm){
        Mono<CoordonneesDTO> monoCoordonneesDTO = geocodingService.getCoordinates("ton adresse");

        // .blockOptional() va bloquer le thread courant jusqu'à ce que la réponse arrive
        Optional<CoordonneesDTO> OptCoordonneesDTO = monoCoordonneesDTO.blockOptional();

        if (OptCoordonneesDTO.isPresent()) {
            CoordonneesDTO coordonneesDTO = OptCoordonneesDTO.get();
            List<StationCarburant> stations = stationCarburantRepository.findStationsProches(new BigDecimal(coordonneesDTO.latitude()), new BigDecimal(coordonneesDTO.longitude()), rayonKm);;
            return formatStationsProches(stations);
        } else {
            return "Coordonnées introuvables pour l'adresse : " + adresse;
        }
    }

    public String formatStationsProches(List<StationCarburant> stations) {
        StringBuilder sb = new StringBuilder();

        stations.forEach(station -> {
            // Utilisation du Text Block pour structurer le contenu
            sb.append(String.format("""
            Station trouvée : %s, %s
            """, station.getAdresse(), station.getVille()));

            station.getPrixCarburants().forEach(prix -> {
                sb.append(String.format("""
                 - %s : %s€
            """, prix.getCarburant(), prix.getValeur()));
            });

            sb.append("\n"); // Ajoute un saut de ligne entre les stations
        });

        return sb.toString();
    }
}
