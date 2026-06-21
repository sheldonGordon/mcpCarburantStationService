package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.PrixCarburant;
import fr.chatelain.mcp.carburantstationservice.model.CarburantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PrixCarburantRepository extends JpaRepository<PrixCarburant, Long> {
    
    List<PrixCarburant> findByStation_Id(Long stationId);
    
    List<PrixCarburant> findByCarburant(CarburantType carburant);
    
    @Query("SELECT p FROM PrixCarburant p WHERE p.station.id = :stationId AND p.carburant = :carburant ORDER BY p.maj DESC")
    List<PrixCarburant> findLatestPriceByStationAndCarburant(@Param("stationId") Long stationId, @Param("carburant") CarburantType carburant);
    
    @Query("SELECT p FROM PrixCarburant p WHERE p.station.id = :stationId ORDER BY p.maj DESC")
    List<PrixCarburant> findLatestPricesByStation(@Param("stationId") Long stationId);
    
    @Query("SELECT p FROM PrixCarburant p WHERE p.maj >= :depuis ORDER BY p.maj DESC")
    List<PrixCarburant> findPricesUpdatedAfter(@Param("depuis") LocalDateTime depuis);
}

