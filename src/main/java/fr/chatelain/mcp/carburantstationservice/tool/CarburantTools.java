package fr.chatelain.mcp.carburantstationservice.tool;

import fr.chatelain.mcp.carburantstationservice.exception.AmbiguousAddressException;
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
import java.util.Objects;
import java.util.Optional;

@Component
@AllArgsConstructor
public class CarburantTools {

    private GeocodingService geocodingService;
    private StationCarburantRepository stationCarburantRepository;

    @McpTool(description = "Donne les stations services les proches dans un rayon de 1km")
    public String prixCarburantAutourAdresse(
            @McpToolParam(description = "Adresse à partir de laquelle chercher les stations services", required = true) String adresse){
        try{
            Mono<CoordonneesDTO> monoCoordonneesDTO = geocodingService.getCoordinates(adresse);

            // .blockOptional() va bloquer le thread courant jusqu'à ce que la réponse arrive
            Optional<CoordonneesDTO> OptCoordonneesDTO = monoCoordonneesDTO.blockOptional();

            if (OptCoordonneesDTO.isPresent()) {
                CoordonneesDTO coordonneesDTO = OptCoordonneesDTO.get();
                List<StationCarburant> stations = stationCarburantRepository.findStationsProches(coordonneesDTO.latitude(), coordonneesDTO.longitude(), 1000.0);
                return formatStationsProches(stations);
            } else {
                return "Coordonnées introuvables pour l'adresse : " + adresse;
            }
        } catch (AmbiguousAddressException e) {
            List<String> choix = e.getSuggestions();
            return "Veuillez choisir parmi : " + String.join(", ", choix);
        }

    }

    @McpTool(description = "Donne les stations services les proches dans un rayon définit par l'utilisateur")
    public String prixCarburantAutourAdresseKilomtre(
            @McpToolParam(description = "Adresse à partir de laquelle chercher les stations services", required = true) String adresse,
            @McpToolParam(description = "Rayon de recherche en kilomètres", required = true) double rayonKm){
        try{
            Mono<CoordonneesDTO> monoCoordonneesDTO = geocodingService.getCoordinates(adresse);

            // .blockOptional() va bloquer le thread courant jusqu'à ce que la réponse arrive
            Optional<CoordonneesDTO> OptCoordonneesDTO = monoCoordonneesDTO.blockOptional();

            if (OptCoordonneesDTO.isPresent()) {
                CoordonneesDTO coordonneesDTO = OptCoordonneesDTO.get();
                List<StationCarburant> stations = stationCarburantRepository.findStationsProches(coordonneesDTO.latitude(), coordonneesDTO.longitude(), rayonKm * 1000.0);
                return formatStationsProches(stations);
            } else {
                return "Coordonnées introuvables pour l'adresse : " + adresse;
            }
        } catch (AmbiguousAddressException e) {
            List<String> choix = e.getSuggestions();
            return "Veuillez choisir parmi : " + String.join(", ", choix);
        }
    }

    public String formatStationsProches(List<StationCarburant> stations) {
        if(Objects.isNull(stations) || stations.isEmpty()) {
            return "Aucune station trouvée dans le rayon spécifié.";
        }
        StringBuilder sb = new StringBuilder();

        stations.forEach(station -> {
            // Utilisation du Text Block pour structurer le contenu
            sb.append(String.format("""
            Station trouvée : %s, %s
            """, station.getAdresse(), station.getVille()));

            // Vérification de sécurité : on ne parcourt que si la liste n'est pas nulle
            if (station.getPrixCarburants() != null) {
                station.getPrixCarburants().forEach(prix -> {
                    sb.append(String.format("""
             - %s : %s€
            """, prix.getCarburant(), prix.getValeur()));
                });
            } else {
                sb.append(" - Aucun prix disponible pour cette station.\n");
            }

            sb.append("\n"); // Ajoute un saut de ligne entre les stations
        });

        return sb.toString();
    }
}
