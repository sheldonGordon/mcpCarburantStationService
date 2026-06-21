package fr.chatelain.mcp.carburantstationservice.scheduler;

import fr.chatelain.mcp.carburantstationservice.service.CarburantDataLoaderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler pour mettre à jour les données de carburants
 * S'exécute tous les jours à 3h du matin
 */
@Slf4j
@Component
public class CarburantDataScheduler {


    @Autowired
    private CarburantDataLoaderService dataLoaderService;

    /**
     * Charge les données de carburants chaque jour à 3h du matin
     * Expression cron: "0 0 3 * * *" = 3:00:00 chaque jour
     */
    @Scheduled(cron = "0 0 3 * * *")
    public void loadCarburantDataDaily() {
        log.info("🔄 Démarrage du chargement quotidien des données de carburants à 3h du matin");
        try {
            dataLoaderService.loadCarburantData();
            log.info("✓ Chargement quotidien terminé avec succès");
        } catch (Exception e) {
            log.error("✗ Erreur lors du chargement quotidien des données", e);
        }
    }

    /**
     * Méthode alternative pour tester manuellement
     * Décommentez cette ligne pour tester lors du démarrage
     * @Scheduled(initialDelay = 5000) // Attendre 5 secondes après le démarrage
     */
    @Scheduled(initialDelay = 5000)
    public void loadCarburantDataOnStartup() {
        log.info("🔄 Test de chargement des données au démarrage");
        try {
            dataLoaderService.loadCarburantData();
            log.info("✓ Chargement au démarrage terminé avec succès");
        } catch (Exception e) {
            log.error("✗ Erreur lors du chargement des données au démarrage", e);
        }
    }
}

