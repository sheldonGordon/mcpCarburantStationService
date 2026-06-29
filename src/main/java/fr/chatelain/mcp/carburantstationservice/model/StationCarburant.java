package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexType;
import org.springframework.data.mongodb.core.index.GeoSpatialIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

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
    @GeoSpatialIndexed(type = GeoSpatialIndexType.GEO_2DSPHERE)
    private GeoJsonPoint location;
    private String codePostal;
    private String adresse;
    private String ville;
    private String departement;
    private String codeDepartement;
    private boolean automate24x24;

    private Horaire horaires;
    private List<Service> services;
    private List<PrixCarburant> prixCarburants;

    public void addPrixCarburant(PrixCarburant newPrix) {
        if(Objects.nonNull(this.prixCarburants)) {
            this.prixCarburants.add(newPrix);
        } else {
            this.prixCarburants = List.of(newPrix);
        }
    }

    public void upsertPrixCarburant(PrixCarburant nouveauPrix) {
        // Cherche le prix existant par type de carburant
        Optional<PrixCarburant> existing = this.prixCarburants.stream()
                .filter(p -> p.getCarburant().equals(nouveauPrix.getCarburant()))
                .findFirst();

        if (existing.isPresent()) {
            // Mise à jour de l'existant
            PrixCarburant prixToUpdate = existing.get();
            prixToUpdate.setValeur(nouveauPrix.getValeur());
        } else {
            // Ajout du nouveau
            this.prixCarburants.add(nouveauPrix);
        }
    }
}

