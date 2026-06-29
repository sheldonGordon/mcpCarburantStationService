package fr.chatelain.mcp.carburantstationservice.service;
import fr.chatelain.mcp.carburantstationservice.exception.AmbiguousAddressException;
import fr.chatelain.mcp.carburantstationservice.model.dto.adresse.CoordonneesDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.adresse.GeocodingResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class GeocodingService {

    private final WebClient adresseWebClient;

    public GeocodingService(WebClient adresseWebClient) {
        this.adresseWebClient = adresseWebClient;
    }

    public Mono<CoordonneesDTO> getCoordinates(String adresse) {
        return adresseWebClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/search")
                        .queryParam("q", adresse)
                        .queryParam("limit", 1)
                        .build())
                .retrieve()
                .bodyToMono(GeocodingResponseDTO.class)
                .flatMap(response -> {
                    var features = response.features();

                    if (features == null || features.isEmpty()) {
                        // On retourne un Mono vide conforme au type attendu
                        return Mono.empty();
                    }

                    // Si on a plusieurs résultats probables, on lève l'exception
                    if (features.size() > 1) {
                        List<String> labels = features.stream()
                                .map(f -> f.properties().label())
                                .toList();
                        return Mono.error(new AmbiguousAddressException(labels));
                    }

                    var coords = features.getFirst().geometry().coordinates();
                    CoordonneesDTO result = new CoordonneesDTO(coords[1], coords[0]);

                    // On retourne le résultat enveloppé dans un Mono
                    return Mono.just(result);
                });
    }
}