package fr.unice.polytech.sophiatecheats.domain.services.suggestions;

import java.util.*;

/**
 * Suggests dish categories/types based on input text using keyword mapping.
 */
public class KeywordBasedSuggestionStrategy implements SuggestionStrategy {
    private static final String VEGAN = "Vegan";
    private static final String ENTREE = "Entrée";
    private static final String MAIN = "Plat principal";
    private static final String PROTEINE = "Protéiné";
    private static final String SUCRE = "Sucré";
    private static final String DESSERT = "Dessert";
    private static final String AUTRE = "Autre";

    private final Map<String, List<String>> keywordMap;

    public KeywordBasedSuggestionStrategy() {
        keywordMap = new HashMap<>();
        keywordMap.put("salade", List.of(ENTREE, VEGAN));
        keywordMap.put("légume", List.of(ENTREE, VEGAN));
        keywordMap.put("vegan", List.of(ENTREE, VEGAN));
        keywordMap.put("poulet", List.of(MAIN, PROTEINE));
        keywordMap.put("boeuf", List.of(MAIN, PROTEINE));
        keywordMap.put("poisson", List.of(MAIN, PROTEINE));
        keywordMap.put("gâteau", List.of(DESSERT, SUCRE));
        keywordMap.put("dessert", List.of(DESSERT, SUCRE));
        keywordMap.put("sucré", List.of(DESSERT, SUCRE));
    }

    @Override
    public List<String> suggest(String inputText) {
        List<String> suggestions = new ArrayList<>();
        if (inputText == null || inputText.isBlank()) return suggestions;
        String lower = inputText.toLowerCase();
        for (Map.Entry<String, List<String>> entry : keywordMap.entrySet()) {
            if (lower.contains(entry.getKey())) {
                for (String s : entry.getValue()) {
                    if (!suggestions.contains(s)) suggestions.add(s);
                }
            }
        }
        if (suggestions.isEmpty()) suggestions.add(AUTRE);
        return suggestions;
    }
}
