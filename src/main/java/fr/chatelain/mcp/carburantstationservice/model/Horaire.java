package fr.chatelain.mcp.carburantstationservice.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.persistence.CascadeType;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "horaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Horaire {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "automate_24x24")
    private boolean automate24x24;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "horaire")
    private List<JourHoraire> jours;
}





