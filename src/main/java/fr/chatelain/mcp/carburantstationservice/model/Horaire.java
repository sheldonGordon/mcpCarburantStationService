package fr.chatelain.mcp.carburantstationservice.model;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;

import java.util.List;

@Document(collection = "horaires")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Horaire {
    @Id
    private String id;
    private String automate24x24;
    private List<JourHoraire> jours;
}

