package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@Table(name = "jour_horaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class JourHoraire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "nom", length = 50, nullable = false)
    private String nom; // Lundi, Mardi, Mercredi, etc.
    @Column(name = "ferme", length = 1)
    private boolean ferme; // Indique si fermé ce jour
    
    @ManyToOne
    private Horaire horaire;
}


