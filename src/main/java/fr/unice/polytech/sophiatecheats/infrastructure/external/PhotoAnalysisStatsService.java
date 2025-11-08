package fr.unice.polytech.sophiatecheats.infrastructure.external;

import java.util.HashMap;
import java.util.Map;

public class PhotoAnalysisStatsService {
    private final Map<String, Integer> analysisCountByCategory = new HashMap<>();
    private final Map<String, Double> precisionByCategory = new HashMap<>();

    public void recordAnalysis(String category, boolean correct) {
        analysisCountByCategory.put(category, analysisCountByCategory.getOrDefault(category, 0) + 1);
        // Simule la précision (ici, toujours 100% si correct, sinon baisse)
        double prev = precisionByCategory.getOrDefault(category, 1.0);
        double newPrecision = correct ? prev : Math.max(0, prev - 0.05);
        precisionByCategory.put(category, newPrecision);
    }

    public int getAnalysisCount(String category) {
        return analysisCountByCategory.getOrDefault(category, 0);
    }

    public double getPrecision(String category) {
        return precisionByCategory.getOrDefault(category, 1.0);
    }

    public Map<String, Double> getAllPrecisions() {
        return precisionByCategory;
    }
}

