package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.math.BigDecimal;

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
    @Enumerated(EnumType.STRING)
    @Column(name = "carburant", nullable = false)
    private CarburantType carburant;
    @Column(name = "maj")
    private LocalDateTime maj;
    @Column(name = "valeur", precision = 10, scale = 3)
    private BigDecimal valeur;
    
    @ManyToOne
    private StationCarburant station;
}



