package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import picocli.CommandLine;
import picocli.CommandLine.Command;

/**
 * Point d'entrée principal de l'application SophiaTech Eats.
 * Utilise l'architecture Clean avec injection de dépendances.
 */
@Command(name = "sophiatech-eats",
         description = "Système de commande et livraison de repas pour le campus SophiaTech",
         version = "1.0.0")
public class SophiaTechEatsApplication implements Runnable {

    private final ApplicationConfig config;

    public SophiaTechEatsApplication() {
        this.config = new ApplicationConfig();
    }

    @Override
    public void run() {
        System.out.println("Démarrage de SophiaTechEats...");
        System.out.println("Application prête");
    }

    public static void main(String[] args) {
        SophiaTechEatsApplication app = new SophiaTechEatsApplication();

        CommandLine cmd = new CommandLine(app);
        int exitCode = cmd.execute(args);

        System.exit(exitCode);
    }

    /**
     * Accès à la configuration pour les tests.
     */
    public ApplicationConfig getConfig() {
        return config;
    }
}
