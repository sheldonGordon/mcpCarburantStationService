package fr.chatelain.mcp.carburantstationservice.model;

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public enum CarburantType {
    GAZOLE("Gazole"),
    SP95("SP95"),
    E85("E85"),
    GPLC("GPLc"),
    E10("E10"),
    SP98("SP98");

    private final String label;

    CarburantType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    // Cache pour la recherche rapide
    private static final Map<String, CarburantType> BY_LABEL =
            Arrays.stream(values()).collect(Collectors.toMap(CarburantType::getLabel, e -> e));

    // Méthode de recherche sécurisée
    public static Optional<CarburantType> fromLabel(String label) {
        return Optional.ofNullable(BY_LABEL.get(label));
    }
}

