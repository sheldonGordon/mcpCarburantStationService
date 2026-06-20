package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "prix_carburant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class PrixCarburant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "carburant", nullable = false)
    private CarburantType carburant;
    @Column(name = "maj")
    private LocalDateTime maj;
    @Column(name = "valeur", precision = 10, scale = 3)
    private Double valeur;
    
    @ManyToOne
    private StationCarburant station;
}



