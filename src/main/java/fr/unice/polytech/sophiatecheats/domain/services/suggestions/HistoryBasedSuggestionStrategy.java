package fr.unice.polytech.sophiatecheats.domain.services.suggestions;

import java.util.List;

/**
 * Suggests dish categories/types based on restaurant's dish history.
 * (Stub for future implementation)
 */
public class HistoryBasedSuggestionStrategy implements SuggestionStrategy {
    @Override
    public List<String> suggest(String inputText) {
        // Implémentation basique : retourne une suggestion fictive selon l'entrée
        if (inputText != null && inputText.toLowerCase().contains("poulet")) {
            return List.of("Plat principal", "Protéiné");
        }
        if (inputText != null && inputText.toLowerCase().contains("salade")) {
            return List.of("Entrée", "Vegan");
        }
        // Par défaut, retourne une catégorie générique
        return List.of("Autre");
    }
}
