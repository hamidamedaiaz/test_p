package fr.unice.polytech.sophiatecheats.domain.services.suggestions;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import fr.unice.polytech.sophiatecheats.domain.services.suggestions.KeywordBasedSuggestionStrategy;

class SuggestionStrategyTest {
    static class MockSuggestionStrategy implements SuggestionStrategy {
        @Override
        public List<String> suggest(String inputText) {
            if (inputText == null || inputText.isBlank()) return List.of();
            return List.of("Category: Entrée", "Tag: Vegan", "Description: Fresh and light");
        }
    }

    @Test
    void testSuggestReturnsExpectedSuggestions() {
        SuggestionStrategy strategy = new MockSuggestionStrategy();
        List<String> suggestions = strategy.suggest("Salade");
        assertFalse(suggestions.isEmpty());
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Category")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Tag")));
        assertTrue(suggestions.stream().anyMatch(s -> s.contains("Description")));
    }

    @Test
    void testSuggestWithEmptyInputReturnsEmptyList() {
        SuggestionStrategy strategy = new MockSuggestionStrategy();
        assertTrue(strategy.suggest("").isEmpty());
        assertTrue(strategy.suggest(null).isEmpty());
    }

    static List<Object[]> keywordTestCases() {
        return List.of(
            new Object[]{"Salade de légumes vegan", List.of("Entrée", "Vegan")},
            new Object[]{"Poulet rôti", List.of("Plat principal", "Protéiné")},
            new Object[]{"Gâteau sucré", List.of("Dessert", "Sucré")},
            new Object[]{"Pizza italienne", List.of("Autre")},
            new Object[]{"", List.of()},
            new Object[]{null, List.of()}
        );
    }

    @ParameterizedTest
    @MethodSource("keywordTestCases")
    void testKeywordBasedSuggest(String input, List<String> expected) {
        SuggestionStrategy strategy = new KeywordBasedSuggestionStrategy();
        List<String> suggestions = strategy.suggest(input);
        assertEquals(expected, suggestions);
    }
}
