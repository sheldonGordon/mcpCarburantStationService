package fr.chatelain.mcp.carburantstationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO pour mapper le JSON de l'API gouvernementale
 * https://data.economie.gouv.fr/api/explore/v2.1/catalog/datasets/prix-carburants-quotidien/exports/json
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class CarburantJsonDTO {
    
    @JsonProperty("id")
    private String id;
    
    @JsonProperty("cp")
    private String codePostal;
    
    @JsonProperty("pop")
    private String pop;
    
    @JsonProperty("adresse")
    private String adresse;
    
    @JsonProperty("ville")
    private String ville;
    
    @JsonProperty("fermeture")
    private LocalDateTime fermeture;
    
    @JsonProperty("geom")
    private GeomDTO geom;
    
    @JsonProperty("prix_maj")
    private LocalDateTime prixMaj;
    
    @JsonProperty("prix_id")
    private String prixId;
    
    @JsonProperty("prix_valeur")
    private Double prixValeur;
    
    @JsonProperty("prix_nom")
    private String prixNom;
    
    @JsonProperty("com_arm_code")
    private String communeCode;
    
    @JsonProperty("reg_code")
    private String regCode;
    
    @JsonProperty("reg_name")
    private String regName;
    
    @JsonProperty("dep_code")
    private String depCode;
    
    @JsonProperty("dep_name")
    private String depName;
    
    @JsonProperty("epci_code")
    private String epciCode;
    
    @JsonProperty("epci_name")
    private String epciName;
    
    @JsonProperty("com_arm_name")
    private String communeName;
    
    @JsonProperty("services_service")
    private List<String> services;
    
    @JsonProperty("rupture_nom")
    private String ruptureNom;
    
    @JsonProperty("rupture_debut")
    private LocalDateTime rupturDebut;
    
    @JsonProperty("rupture_fin")
    private LocalDateTime ruptureFin;
    
    @JsonProperty("horaires_automate_24_24")
    private String horairesAutomate24x24;
    
    @JsonProperty("horaires")
    private String horairesJson;
    
    @JsonProperty("rupture")
    private String ruptureJson;
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class GeomDTO {
        @JsonProperty("lon")
        private Double lon;
        
        @JsonProperty("lat")
        private Double lat;
    }
}

