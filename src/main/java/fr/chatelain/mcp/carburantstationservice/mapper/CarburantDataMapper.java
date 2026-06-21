package fr.chatelain.mcp.carburantstationservice.mapper;

import fr.chatelain.mcp.carburantstationservice.model.*;
import fr.chatelain.mcp.carburantstationservice.model.dto.CarburantJsonDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.HorairesDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.JourDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.RuptureDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Mapper pour convertir les DTOs JSON en entités JPA
 */
@Slf4j
@Component
public class CarburantDataMapper {

    /**
     * Mappe un DTO JSON vers une entité StationCarburant
     */
    public StationCarburant mapToStationCarburant(CarburantJsonDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            StationCarburant station = StationCarburant.builder()
                    .id(Long.parseLong(dto.id()))
                    .codePostal(dto.codePostal())
                    .adresse(dto.adresse())
                    .ville(dto.ville())
                    .departement(dto.depName())
                    .codeDepartement(dto.depCode())
                    .automate24x24(isAutomate24x24(dto.horairesAutomate24x24()))
                    .latitude(dto.geom() != null ? dto.geom().lat() : null)
                    .longitude(dto.geom() != null ? dto.geom().lon() : null)
                    .build();

            // Ajouter les services
            if (dto.services() != null && !dto.services().isEmpty()) {
                List<Service> serviceList = new ArrayList<>();
                for (String serviceName : dto.services()) {
                    Service service = Service.builder()
                            .nom(serviceName)
                            .build();
                    serviceList.add(service);
                }
                station.setServices(serviceList);
            }

            // TODO Ajouter les prix si présents


            // Ajouter les ruptures si présentes
            if (dto.rupture() != null) {
                RuptureCarburant rupture = mapToRuptureCarburant(dto);
                station.setRupture(rupture);
            }

            // Ajouter les horaires si présents
            if (dto.horairesJson() != null) {
                Horaire horaire = mapToHoraire(dto.horairesJson());
                station.setHoraires(horaire);
            }

            return station;
        } catch (Exception e) {
            log.error("Erreur lors du mapping de la station: {}", dto.id(), e);
            return null;
        }
    }

    private static RuptureCarburant mapToRuptureCarburant(CarburantJsonDTO dto) {
        return RuptureCarburant.builder()
                .carburant(CarburantType.valueOf(dto.rupture().nom()))
                .debut(dto.rupture().debut().toLocalDateTime())
                .fin(dto.rupture().fin().toLocalDateTime())
                .type(dto.rupture().type())
                .build();
    }

    /**
     * Mappe les horaires JSON vers une entité Horaire
     */
    private Horaire mapToHoraire(HorairesDTO horairesJson) {
        Horaire horaire = Horaire.builder()
                .automate24x24(horairesJson.automate2424())
                .build();

        // Le parsing complet du JSON des horaires nécessiterait un parser JSON
        // Pour l'instant, on crée les 7 jours de base
        List<JourHoraire> jours = new ArrayList<>();

        for (JourDTO jourDTO : horairesJson.jours()) {
            JourHoraire jour = JourHoraire.builder()
                    .nom(jourDTO.nom())
                    .ferme(jourDTO.ferme())
                    .build();
            jours.add(jour);
        }

        horaire.setJours(jours);
        return horaire;
    }

    /**
     * Convertit la chaîne "Oui"/"Non" en booléen
     */
    private boolean isAutomate24x24(String value) {
        return "Oui".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }

    /**
     * Extrait le type de rupture du JSON
     */
    private String extractRuptureType(String ruptureJson) {
        if (ruptureJson != null && ruptureJson.contains("\"@type\": \"definitive\"")) {
            return "definitive";
        }
        return "temporaire";
    }

    public PrixCarburant mapToPrixCarburant(CarburantJsonDTO dto, StationCarburant stationCarburant) {
        if (dto == null || stationCarburant == null) {
            return null;
        }

        try {
            CarburantType carburantType = CarburantType.fromLabel(dto.prixNom())
                    .orElseThrow(() -> new IllegalArgumentException("Type de carburant non reconnu: " + dto.prixNom()));

            PrixCarburant prixCarburant = PrixCarburant.builder()
                    .idStation(stationCarburant.getId())
                    .carburant(carburantType)
                    .valeur(dto.prixValeur() != null ? new BigDecimal(dto.prixValeur()) : null)
                    .build();

            return prixCarburant;
        } catch (Exception e) {
            log.error("Erreur lors du mapping du prix carburant pour la station: {}", stationCarburant.getId(), e);
            return null;
        }
    }
}