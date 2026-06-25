package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface StationCarburantRepository extends MongoRepository<StationCarburant, Long> {
    
    List<StationCarburant> findByVille(String ville);
    
    List<StationCarburant> findByDepartement(String departement);
    
    List<StationCarburant> findByCodePostalStartingWith(String codePostal);
    
    @Query("{ 'automate24x24': true }")
    List<StationCarburant> findAllAutomate24x24();
    
    @Query("{ 'region': ?0 }")
    List<StationCarburant> findByRegion(String region);
    
    /**
     * Recherche les stations carburant dans un périmètre donné autour d'une latitude/longitude
     */
    @Query("{ $expr: { $lte: [ { $sqrt: { $add: [ { $pow: [ { $multiply: [ { $subtract: [ '$latitude', ?0 ] }, 111.2 ] }, 2 ] }, { $pow: [ { $multiply: [ { $subtract: [ '$longitude', ?1 ] }, 111.2 ] }, 2 ] } ] } }, ?2 ] } }")
    List<StationCarburant> findStationsProches(BigDecimal latitude, BigDecimal longitude, double perimetre);
}

