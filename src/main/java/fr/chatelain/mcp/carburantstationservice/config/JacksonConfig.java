package fr.chatelain.mcp.carburantstationservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // On enregistre le module pour que Jackson comprenne LocalDateTime (Java 8+)
        mapper.registerModule(new JavaTimeModule());

        // Optionnel mais très recommandé : écrire les dates au format ISO (ex: "2026-06-21T15:30:00")
        // plutôt qu'en timestamp (suite de chiffres illisibles)
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        return mapper;
    }
}
