package fr.chatelain.mcp.carburantstationservice.mapper;

import fr.chatelain.mcp.carburantstationservice.model.*;
import fr.chatelain.mcp.carburantstationservice.model.dto.carburant.CarburantJsonDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.carburant.HorairesDTO;
import fr.chatelain.mcp.carburantstationservice.model.dto.carburant.JourDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
                    .latitude(Objects.nonNull(dto.geom()) ? bigDecimalFormatForMongo(dto.geom().lat()) : null)
                    .longitude(Objects.nonNull(dto.geom()) ? bigDecimalFormatForMongo(dto.geom().lon()) : null)
                    .build();

            // Ajouter les services
            if (Objects.nonNull(dto.services()) && !dto.services().isEmpty()) {
                List<Service> serviceList = new ArrayList<>();
                for (String serviceName : dto.services()) {
                    Service service = Service.builder()
                            .nom(serviceName)
                            .build();
                    serviceList.add(service);
                }
                station.setServices(serviceList);
            }

            // Ajouter le prix si présents
            if(Objects.nonNull(dto.prixNom()) && Objects.nonNull(dto.prixValeur())) {
                PrixCarburant prixCarburant = mapToPrixCarburant(dto, station);
                if (Objects.nonNull(prixCarburant)) {
                    List<PrixCarburant> prixList = new ArrayList<>();
                    prixList.add(prixCarburant);
                    station.setPrixCarburants(prixList);
                }
            }

            // Ajouter les horaires si présents
            if (Objects.nonNull(dto.horairesJson())) {
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

        if (Objects.nonNull(horairesJson.jours())) {
            List<JourHoraire> jours = new ArrayList<>();

            for (JourDTO jourDTO : horairesJson.jours()) {
                JourHoraire jour = JourHoraire.builder()
                        .nom(jourDTO.nom())
                        .ferme(jourDTO.ferme())
                        .build();
                jours.add(jour);
            }

            horaire.setJours(jours);
        }
        return horaire;
    }

    /**
     * Convertit la chaîne "Oui"/"Non" en booléen
     */
    private boolean isAutomate24x24(String value) {
        return "Oui".equalsIgnoreCase(value) || "yes".equalsIgnoreCase(value) || "true".equalsIgnoreCase(value);
    }

    public PrixCarburant mapToPrixCarburant(CarburantJsonDTO dto, StationCarburant stationCarburant) {
        if (Objects.isNull(dto) || Objects.isNull(stationCarburant)) {
            return null;
        }

        try {
            CarburantType carburantType = CarburantType.fromLabel(dto.prixNom())
                    .orElseThrow(() -> new IllegalArgumentException("Type de carburant non reconnu: " + dto.prixNom()));

            return PrixCarburant.builder()
                    .idStation(stationCarburant.getId())
                    .carburant(carburantType)
                    .valeur(Objects.nonNull(dto.prixValeur()) ? bigDecimalFormatForMongo(dto.prixValeur()) : null)
                    .build();
        } catch (Exception e) {
            log.error("Erreur lors du mapping du prix carburant pour la station: {}", stationCarburant.getId(), e);
            return null;
        }
    }

    private BigDecimal bigDecimalFormatForMongo(Double value) {
        if (value == null){
            return null;
        }

        return BigDecimal.valueOf(value)
                .setScale(8, java.math.RoundingMode.HALF_UP)
                .stripTrailingZeros();
    }
}