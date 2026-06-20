package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.*;

/**
 * Classe représentant un service proposé par une station carburant
 */
@Entity
@Table(name = "service")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nom", length = 255, nullable = false)
    private String nom; // Relais colis, Station de gonflage, Aire de camping-cars, etc.
    
    @ManyToOne
    private StationCarburant station;
}

