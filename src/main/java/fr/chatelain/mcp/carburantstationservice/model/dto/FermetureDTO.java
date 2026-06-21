package fr.chatelain.mcp.carburantstationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.chatelain.mcp.carburantstationservice.config.FlexibleOffsetDateTimeDeserializer;

import java.time.OffsetDateTime;

@JsonIgnoreProperties(ignoreUnknown = true)
public record FermetureDTO (
    @JsonProperty("@type") String type,
    @JsonProperty("@debut") @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class) OffsetDateTime debut,
    @JsonProperty("@fin") @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class) OffsetDateTime fin
) {}
