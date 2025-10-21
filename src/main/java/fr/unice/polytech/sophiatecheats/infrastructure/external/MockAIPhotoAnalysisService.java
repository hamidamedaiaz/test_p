package fr.unice.polytech.sophiatecheats.infrastructure.external;

import fr.unice.polytech.sophiatecheats.domain.services.photoai.PhotoAnalysisService;
import fr.unice.polytech.sophiatecheats.application.dto.photoai.PhotoAnalysisResult;
import java.util.Arrays;

public class MockAIPhotoAnalysisService implements PhotoAnalysisService {
    @Override
    public PhotoAnalysisResult analyze(byte[] imageData) {
        // Simulation d'analyse IA : retourne des valeurs fixes ou pseudo-aléatoires
        String description = "Plat appétissant avec légumes frais.";
        String category = "plat";
        return new PhotoAnalysisResult(description, category, Arrays.asList("végétarien", "sans gluten"));
    }
}

