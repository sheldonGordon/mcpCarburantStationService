package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.RuptureCarburant;
import fr.chatelain.mcp.carburantstationservice.model.CarburantType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface RuptureCarburantRepository extends MongoRepository<RuptureCarburant, String> {
    
    List<RuptureCarburant> findByCarburant(CarburantType carburant);
    
    @Query("{ 'debut': { $lte: ?0 }, 'fin': { $gte: ?0 } }")
    List<RuptureCarburant> findActiveRuptures(LocalDateTime now);
}

