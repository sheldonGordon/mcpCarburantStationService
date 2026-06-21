package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.RuptureCarburant;
import fr.chatelain.mcp.carburantstationservice.model.CarburantType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface RuptureCarburantRepository extends JpaRepository<RuptureCarburant, Long> {
    
    List<RuptureCarburant> findByStation_Id(Long stationId);
    
    List<RuptureCarburant> findByCarburant(CarburantType carburant);
    
    List<RuptureCarburant> findByStatut(String statut);
    
    @Query("SELECT r FROM RuptureCarburant r WHERE r.station.id = :stationId AND r.carburant = :carburant")
    List<RuptureCarburant> findByStationAndCarburant(@Param("stationId") Long stationId, @Param("carburant") CarburantType carburant);
    
    @Query("SELECT r FROM RuptureCarburant r WHERE r.debut <= :now AND r.fin >= :now")
    List<RuptureCarburant> findActiveRuptures(@Param("now") LocalDateTime now);
}

