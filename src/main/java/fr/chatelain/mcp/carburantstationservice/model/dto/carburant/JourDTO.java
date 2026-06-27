package fr.chatelain.mcp.carburantstationservice.model.dto.carburant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.chatelain.mcp.carburantstationservice.config.StringToListDeserializer;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JourDTO (
        @JsonProperty("@id") String id,
        @JsonProperty("@nom") String nom,
        @JsonProperty("@ferme") String ferme,
        @JsonProperty("horaire") @JsonDeserialize(using = StringToListDeserializer.class) List<HoraireDTO> horaires
        ){}
