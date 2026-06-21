package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;

/**
 * Classe représentant un service proposé par une station carburant
 */
@Document(collection = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Service {
    @Id
    private String id;
    private String nom;
}

