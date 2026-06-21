package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;

@Document(collection = "jour_horaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JourHoraire {
    @Id
    private String id;
    private String nom;
    private String ferme;
}

