package fr.unice.polytech.sophiatecheats.domain.services.suggestions;

import java.util.List;

/**
 * Strategy interface for intelligent dish input suggestions (categories, tags, description, etc).
 * Extensible for new suggestion strategies without modifying existing code.
 */
public interface SuggestionStrategy {
    /**
     * Returns a list of suggestions based on the input text.
     * @param inputText The current dish input (name, description, etc).
     * @return List of suggestion strings (categories, tags, etc).
     */
    List<String> suggest(String inputText);
}

