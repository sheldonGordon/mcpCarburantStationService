package fr.chatelain.mcp.carburantstationservice.model.dto.adresse;

import java.util.List;

public record GeocodingResponseDTO (List<FeatureDTO> features)
{}
