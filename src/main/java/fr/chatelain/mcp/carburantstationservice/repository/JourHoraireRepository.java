package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.JourHoraire;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourHoraireRepository extends MongoRepository<JourHoraire, String> {
    
    List<JourHoraire> findByNom(String nom);
}

