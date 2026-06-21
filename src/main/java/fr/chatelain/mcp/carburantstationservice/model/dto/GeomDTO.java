package fr.chatelain.mcp.carburantstationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GeomDTO (
    @JsonProperty("lon") Double lon,
    @JsonProperty("lat") Double lat
) {}
