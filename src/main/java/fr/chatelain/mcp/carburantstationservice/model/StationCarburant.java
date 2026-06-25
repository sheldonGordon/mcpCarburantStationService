package fr.chatelain.mcp.carburantstationservice.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.FieldType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

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
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal latitude;
    @Field(targetType = FieldType.DECIMAL128)
    private BigDecimal longitude;
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
}

