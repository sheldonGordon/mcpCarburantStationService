package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.StationCarburant;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface StationCarburantRepository extends MongoRepository<StationCarburant, Long> {

    /**
     * Recherche les stations carburant dans un périmètre donné autour d'une latitude/longitude
     */
    // $maxDistance est en MÈTRES (10km = 10000)
    @Query("{ 'location': { $nearSphere: { $geometry: { type: 'Point', coordinates: [?1, ?0] }, $maxDistance: ?2 } } }")
    List<StationCarburant> findStationsProches(double lat, double lon, double distanceEnMetres);
}

