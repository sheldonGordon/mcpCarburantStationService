package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    
    List<Service> findByNom(String nom);
    
    List<Service> findByStation_Id(Long stationId);
}

