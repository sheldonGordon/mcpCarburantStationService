package fr.chatelain.mcp.carburantstationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record JourDTO (
    @JsonProperty("@id") String id,
    @JsonProperty("@nom") String nom,
    @JsonProperty("@ferme") String ferme
){}
