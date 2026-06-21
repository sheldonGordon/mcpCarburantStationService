package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "stations_carburant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class StationCarburant {
    @Id
    @Column(name = "id")
    private Long id;
    @Column(name = "latitude", nullable = false)
    private Double latitude;
    @Column(name = "longitude", nullable = false)
    private Double longitude;
    @Column(name = "code_postal", length = 5)
    private String codePostal;
    @Column(name = "pop", length = 1)
    private String pop;
    @Column(name = "adresse", length = 255)
    private String adresse;
    @Column(name = "ville", length = 100)
    private String ville;
    @Column(name = "departement", length = 100)
    private String departement;
    @Column(name = "code_departement", length = 2)
    private String codeDepartement;
    @Column(name = "region", length = 100)
    private String region;
    @Column(name = "code_region", length = 2)
    private String codeRegion;
    @Column(name = "automate_24x24")
    private boolean automate24x24;
    
    @OneToOne(cascade = CascadeType.ALL)
    private Horaire horaires;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private List<Service> services;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private List<PrixCarburant> prix;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "station")
    private List<RuptureCarburant> rupture;
}



