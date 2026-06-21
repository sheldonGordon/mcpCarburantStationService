package fr.chatelain.mcp.carburantstationservice.repository;

import fr.chatelain.mcp.carburantstationservice.model.JourHoraire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourHoraireRepository extends JpaRepository<JourHoraire, Long> {
    
    List<JourHoraire> findByNom(String nom);
    
    List<JourHoraire> findByHoraire_Id(Long horaireId);
    
    @Query("SELECT j FROM JourHoraire j WHERE j.horaire.id = :horaireId AND j.ferme = false")
    List<JourHoraire> findOpenDaysByHoraire(@Param("horaireId") Long horaireId);
}

