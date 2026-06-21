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
                    .pop(dto.pop())
                    .adresse(dto.adresse())
                    .ville(dto.ville())
                    .departement(dto.depName())
                    .codeDepartement(dto.depCode())
                    .region(dto.regName())
                    .codeRegion(dto.regCode())
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

            // Ajouter les prix si présents
            if (dto.prixNom() != null && dto.prixValeur() != null) {
                List<PrixCarburant> prixList = new ArrayList<>();
                try {
                    PrixCarburant prix = PrixCarburant.builder()
                            .carburant(CarburantType.valueOf(dto.prixNom()))
                            .maj(dto.prixMaj().toLocalDateTime())
                            .valeur(BigDecimal.valueOf(dto.prixValeur()))
                            .build();
                    prixList.add(prix);
                    station.setPrix(prixList);
                } catch (IllegalArgumentException e) {
                    log.warn("Type de carburant inconnu: {}", dto.prixNom());
                }
            }

            // Ajouter les ruptures si présentes
            if (dto.rupture() != null && !dto.rupture().isEmpty()) {
                List<RuptureCarburant> ruptureList = new ArrayList<>();
                for (RuptureDTO ruptureDTO : dto.rupture()) {
                    RuptureCarburant rupture = RuptureCarburant.builder()
                            .carburant(CarburantType.valueOf(ruptureDTO.nom()))
                            .debut(ruptureDTO.debut().toLocalDateTime())
                            .fin(ruptureDTO.fin().toLocalDateTime())
                            .type(ruptureDTO.type())
                            .build();
                    ruptureList.add(rupture);
                }
                station.setRupture(ruptureList);
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
}