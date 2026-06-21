package fr.chatelain.mcp.carburantstationservice.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Optional;

/**
 * Classe d'initialisation du schéma PostgreSQL
 * S'exécute AVANT la création de l'EntityManagerFactory
 * Crée automatiquement le schéma au démarrage de l'application
 */
@Slf4j
@Configuration
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SchemaInitializer implements BeanFactoryPostProcessor {

    private static final String SCHEMA_NAME_PROPERTY = "spring.jpa.properties.hibernate.default_schema";

    @Override
    public void postProcessBeanFactory(@NonNull ConfigurableListableBeanFactory beanFactory) {
        try {
            String schemaName = getSchemaName(beanFactory);
            
            if ("public".equals(schemaName)) {
                log.info("Schéma par défaut 'public' utilisé, pas de création nécessaire");
                return;
            }

            // Créer le schéma avant que Hibernate ne l'utilise
            createSchemaIfNotExists(schemaName);

        } catch (Exception e) {
            log.error("Erreur lors de la création du schéma", e);
            throw new RuntimeException("Impossible de créer le schéma", e);
        }
    }

    private String getSchemaName(ConfigurableListableBeanFactory beanFactory) {
        return Optional.ofNullable(beanFactory.resolveEmbeddedValue("${" + SCHEMA_NAME_PROPERTY + ":public}"))
                .orElse("public");
    }

    private void createSchemaIfNotExists(String schemaName) throws SQLException {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {

            // Vérifier si le schéma existe
            String checkSchema = String.format(
                    "SELECT schema_name FROM information_schema.schemata WHERE schema_name = '%s'",
                    schemaName);

            if (!statement.executeQuery(checkSchema).next()) {
                // Le schéma n'existe pas, le créer
                String createSchema = String.format("CREATE SCHEMA IF NOT EXISTS %s", schemaName);
                statement.executeUpdate(createSchema);
                log.info("✓ Schéma '{}' créé avec succès", schemaName);
            } else {
                log.info("✓ Schéma '{}' existe déjà", schemaName);
            }

        } catch (SQLException e) {
            log.error("Erreur lors de la création du schéma '{}': {}", schemaName, e.getMessage());
            throw e;
        }
    }

    private Connection getConnection() throws SQLException {
        // Utiliser les propriétés de spring.datasource pour se connecter
        String url = System.getProperty("spring.datasource.url", 
                System.getenv("SPRING_DATASOURCE_URL"));
        String user = System.getProperty("spring.datasource.username",
                System.getenv("SPRING_DATASOURCE_USERNAME"));
        String password = System.getProperty("spring.datasource.password",
                System.getenv("SPRING_DATASOURCE_PASSWORD"));

        if (url == null) {
            url = "jdbc:postgresql://localhost:5432/mcp_db";
            user = "mcp_user";
            password = "un_mot_de_passe_tres_securise";
        }

        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver PostgreSQL non trouvé", e);
        }

        return java.sql.DriverManager.getConnection(url, user, password);
    }
}

