package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;

import java.util.List;

@Document(collection = "stations_carburant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StationCarburant {
    @Id
    private Long id;
    private Double latitude;
    private Double longitude;
    private String codePostal;
    private String adresse;
    private String ville;
    private String departement;
    private String codeDepartement;
    private boolean automate24x24;
    
    private Horaire horaires;
    private List<Service> services;
    private RuptureCarburant rupture;
    private List<PrixCarburant> prixCarburants;
}

