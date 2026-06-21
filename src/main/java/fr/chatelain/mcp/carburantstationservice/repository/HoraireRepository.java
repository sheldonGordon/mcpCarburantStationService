package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.Horaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HoraireRepository extends JpaRepository<Horaire, Long> {
    
    @Query("SELECT h FROM Horaire h WHERE h.automate24x24 = true")
    List<Horaire> findAllAutomate24x24();
}

