package fr.chatelain.mcp.carburantstationservice.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import fr.chatelain.mcp.carburantstationservice.config.FlexibleOffsetDateTimeDeserializer;
import fr.chatelain.mcp.carburantstationservice.config.StringToJsonDeserializer;
import fr.chatelain.mcp.carburantstationservice.config.StringToListDeserializer;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * DTO pour mapper le JSON de l'API gouvernementale
 * https://data.economie.gouv.fr/api/explore/v2.1/catalog/datasets/prix-carburants-quotidien/exports/json
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record CarburantJsonDTO (
    @JsonProperty("id") String id,
    @JsonProperty("cp") String codePostal,
    @JsonProperty("pop") String pop,
    @JsonProperty("adresse") String adresse,
    @JsonProperty("ville") String ville,
    @JsonProperty("fermeture") @JsonDeserialize(using = StringToListDeserializer.class) List<FermetureDTO> fermeture,
    @JsonProperty("geom") @JsonDeserialize(using = StringToJsonDeserializer.class) GeomDTO geom,
    @JsonProperty("prix_maj") @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class) OffsetDateTime prixMaj,
    @JsonProperty("prix_id") String prixId,
    @JsonProperty("prix_valeur") Double prixValeur,
    @JsonProperty("prix_nom") String prixNom,
    @JsonProperty("com_arm_code") String communeCode,
    @JsonProperty("reg_code") String regCode,
    @JsonProperty("reg_name") String regName,
    @JsonProperty("dep_code") String depCode,
    @JsonProperty("dep_name") String depName,
    @JsonProperty("epci_code") String epciCode,
    @JsonProperty("epci_name") String epciName,
    @JsonProperty("com_arm_name") String communeName,
    @JsonProperty("services_service") List<String> services,
    @JsonProperty("rupture_nom") String ruptureNom,
    @JsonProperty("rupture_debut") @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class) OffsetDateTime rupturDebut,
    @JsonProperty("rupture_fin")  @JsonDeserialize(using = FlexibleOffsetDateTimeDeserializer.class) OffsetDateTime ruptureFin,
    @JsonProperty("horaires_automate_24_24") String horairesAutomate24x24,
    @JsonProperty("horaires") @JsonDeserialize(using = StringToJsonDeserializer.class) HorairesDTO horairesJson,
    @JsonProperty("rupture") @JsonDeserialize(using = StringToListDeserializer.class) List<RuptureDTO> rupture
) {}