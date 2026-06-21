package fr.chatelain.mcp.carburantstationservice.model;

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
}

