package fr.chatelain.mcp.carburantstationservice.mapper;

import fr.chatelain.mcp.carburantstationservice.model.*;
import fr.chatelain.mcp.carburantstationservice.model.dto.CarburantJsonDTO;
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
                    .id(Long.parseLong(dto.getId()))
                    .codePostal(dto.getCodePostal())
                    .pop(dto.getPop())
                    .adresse(dto.getAdresse())
                    .ville(dto.getVille())
                    .departement(dto.getDepName())
                    .codeDepartement(dto.getDepCode())
                    .region(dto.getRegName())
                    .codeRegion(dto.getRegCode())
                    .automate24x24(isAutomate24x24(dto.getHorairesAutomate24x24()))
                    .latitude(dto.getGeom() != null ? dto.getGeom().getLat() : null)
                    .longitude(dto.getGeom() != null ? dto.getGeom().getLon() : null)
                    .build();

            // Ajouter les services
            if (dto.getServices() != null && !dto.getServices().isEmpty()) {
                List<Service> serviceList = new ArrayList<>();
                for (String serviceName : dto.getServices()) {
                    Service service = Service.builder()
                            .nom(serviceName)
                            .station(station)
                            .build();
                    serviceList.add(service);
                }
                station.setServices(serviceList);
            }

            // Ajouter les prix si présents
            if (dto.getPrixNom() != null && dto.getPrixValeur() != null) {
                List<PrixCarburant> prixList = new ArrayList<>();
                try {
                    PrixCarburant prix = PrixCarburant.builder()
                            .carburant(CarburantType.valueOf(dto.getPrixNom()))
                            .maj(dto.getPrixMaj())
                            .valeur(BigDecimal.valueOf(dto.getPrixValeur()))
                            .station(station)
                            .build();
                    prixList.add(prix);
                    station.setPrix(prixList);
                } catch (IllegalArgumentException e) {
                    log.warn("Type de carburant inconnu: {}", dto.getPrixNom());
                }
            }

            // Ajouter les ruptures si présentes
            if (dto.getRuptureNom() != null) {
                List<RuptureCarburant> ruptureList = new ArrayList<>();
                try {
                    RuptureCarburant rupture = RuptureCarburant.builder()
                            .carburant(CarburantType.valueOf(dto.getRuptureNom()))
                            .debut(dto.getRupturDebut())
                            .fin(dto.getRuptureFin())
                            .statut(extractRuptureType(dto.getRuptureJson()))
                            .station(station)
                            .build();
                    ruptureList.add(rupture);
                    station.setRupture(ruptureList);
                } catch (IllegalArgumentException e) {
                    log.warn("Type de carburant rupture inconnu: {}", dto.getRuptureNom());
                }
            }

            // Ajouter les horaires si présents
            if (dto.getHorairesJson() != null) {
                Horaire horaire = mapToHoraire(dto.getHorairesJson(), station);
                station.setHoraires(horaire);
            }

            return station;
        } catch (Exception e) {
            log.error("Erreur lors du mapping de la station: {}", dto.getId(), e);
            return null;
        }
    }

    /**
     * Mappe les horaires JSON vers une entité Horaire
     */
    private Horaire mapToHoraire(String horairesJson, StationCarburant station) {
        Horaire horaire = Horaire.builder()
                .automate24x24(false)
                .build();

        // Le parsing complet du JSON des horaires nécessiterait un parser JSON
        // Pour l'instant, on crée les 7 jours de base
        List<JourHoraire> jours = new ArrayList<>();
        String[] nomsJours = {"Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Dimanche"};
        
        for (int i = 0; i < nomsJours.length; i++) {
            JourHoraire jour = JourHoraire.builder()
                    .nom(nomsJours[i])
                    .ferme(false)
                    .horaire(horaire)
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

