package fr.chatelain.mcp.carburantstationservice.exception;

import java.util.List;

public class AmbiguousAddressException extends RuntimeException {
    private final List<String> suggestions;

    public AmbiguousAddressException(List<String> suggestions) {
        super("Plusieurs adresses trouvées, merci de préciser.");
        this.suggestions = suggestions;
    }

    public List<String> getSuggestions() {
        return suggestions;
    }
}