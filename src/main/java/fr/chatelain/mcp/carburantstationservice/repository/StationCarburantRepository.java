package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StationCarburantRepository extends JpaRepository<StationCarburant, Long> {
    
    List<StationCarburant> findByVille(String ville);
    
    List<StationCarburant> findByDepartement(String departement);
    
    List<StationCarburant> findByCodePostalStartingWith(String codePostal);
    
    @Query("SELECT s FROM StationCarburant s WHERE s.automate24x24 = true")
    List<StationCarburant> findAllAutomate24x24();
    
    @Query("SELECT s FROM StationCarburant s WHERE s.region = :region")
    List<StationCarburant> findByRegion(@Param("region") String region);
    
    /**
     * Recherche les stations carburant dans un périmètre donné autour d'une latitude/longitude
     * 
     * @param latitude latitude du point central
     * @param longitude longitude du point central
     * @param perimetre périmètre de recherche en kilomètres
     * @return liste des stations carburant dans le périmètre
     */
    @Query("SELECT s FROM StationCarburant s WHERE " +
           "SQRT(POWER((s.latitude - :latitude) * 111.2, 2) + " +
           "POWER((s.longitude - :longitude) * 111.2, 2)) <= :perimetre " +
           "ORDER BY SQRT(POWER((s.latitude - :latitude) * 111.2, 2) + " +
           "POWER((s.longitude - :longitude) * 111.2, 2)) ASC")
    List<StationCarburant> findStationsProches(
            @Param("latitude") Long latitude,
            @Param("longitude") Long longitude,
            @Param("perimetre") double perimetre);
}

