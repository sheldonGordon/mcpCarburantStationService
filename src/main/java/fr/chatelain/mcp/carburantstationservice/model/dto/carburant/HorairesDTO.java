package fr.chatelain.mcp.carburantstationservice.model.dto.carburant;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.chatelain.mcp.carburantstationservice.config.StringToJsonDeserializer;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record HorairesDTO (
    @JsonProperty("@automate-24-24") String automate2424,
    @JsonProperty("jour") @JsonDeserialize(using = StringToJsonDeserializer.class) List<JourDTO> jours
) {}
