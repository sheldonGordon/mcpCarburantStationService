package fr.chatelain.mcp.carburantstationservice.service;
import fr.chatelain.mcp.carburantstationservice.model.dto.adresse.CoordonneesDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.adresse.GeocodingResponseDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                    if (response.features() == null || response.features().isEmpty()) {
                        // On retourne un Mono vide conforme au type attendu
                        return Mono.empty();
                    }

                    var coords = response.features().getFirst().geometry().coordinates();
                    CoordonneesDTO result = new CoordonneesDTO(coords[1], coords[0]);

                    // On retourne le résultat enveloppé dans un Mono
                    return Mono.just(result);
                });
    }
}