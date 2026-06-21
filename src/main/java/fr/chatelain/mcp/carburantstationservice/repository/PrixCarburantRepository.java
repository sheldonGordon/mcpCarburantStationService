package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.PrixCarburant;
import fr.chatelain.mcp.carburantstationservice.model.CarburantType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.time.LocalDateTime;

@Repository
public interface PrixCarburantRepository extends MongoRepository<PrixCarburant, String> {
    
    List<PrixCarburant> findByCarburant(CarburantType carburant);
    
    @Query("{ 'maj': { $gte: ?0 } }")
    List<PrixCarburant> findPricesUpdatedAfter(LocalDateTime depuis);
}

