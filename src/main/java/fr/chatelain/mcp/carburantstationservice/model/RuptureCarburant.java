package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "rupture_carburant")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class RuptureCarburant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "carburant", nullable = false)
    private CarburantType carburant;
    @Column(name = "debut")
    private LocalDateTime debut;
    @Column(name = "fin")
    private LocalDateTime fin;
    @Column(name = "statut", length = 20)
    private String statut; // "temporaire" ou "definitive"
    
    @ManyToOne
    private StationCarburant station;
}


