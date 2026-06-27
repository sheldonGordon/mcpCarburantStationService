package fr.chatelain.mcp.carburantstationservice.model.dto.carburant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HoraireDTO(
        @JsonProperty("@ouverture") String ouverture,
        @JsonProperty("@fermeture") String fermeture
) {
}
