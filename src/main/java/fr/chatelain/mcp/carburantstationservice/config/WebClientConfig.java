package fr.chatelain.mcp.carburantstationservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient adresseWebClient() {
        return WebClient.builder()
                .baseUrl("https://api-adresse.data.gouv.fr")
                .build();
    }
}
