package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

/**
 * Demonstration class for US1: Browse restaurants and dishes functionality.
 * 
 * <p>This class showcases the implementation of User Story 1, demonstrating how campus users 
 * can browse available restaurants and their dishes with various filtering options.</p>
 * 
 * <p><strong>US1:</strong> "As a campus user, I want to browse restaurants and dishes 
 * so that I can see what's available for ordering"</p>
 * 
 * <h3>Key Features Demonstrated:</h3>
 * <ul>
 *   <li>Browse all restaurants with their complete dish menus</li>
 *   <li>Filter restaurants by availability status (open/closed)</li>
 *   <li>Filter restaurants by dish categories (vegetarian, main courses, etc.)</li>
 *   <li>Combine multiple filters for refined search results</li>
 * </ul>
 * 
 * <h3>Architecture Implementation:</h3>
 * <p>Follows Clean Architecture principles:</p>
 * <ul>
 *   <li><strong>Domain Layer:</strong> Restaurant and Dish entities, repository interfaces</li>
 *   <li><strong>Application Layer:</strong> BrowseRestaurantsUseCase with DTOs</li>
 *   <li><strong>Infrastructure Layer:</strong> In-memory repository implementation</li>
 * </ul>
 * 
 * <h3>Testing:</h3>
 * <p>The functionality is validated through comprehensive test suites:</p>
 * <ul>
 *   <li>Unit tests for entities and use cases</li>
 *   <li>Integration tests for complete workflow</li>
 *   <li>Cucumber BDD scenarios for acceptance criteria</li>
 * </ul>
 * 
 * @author SophiaTechEats Team
 * @version 1.0
 * @since 1.0
 */
public class US1Demo {

    /**
     * Demonstrates browsing all restaurants without any filters.
     * 
     * @param useCase the browse restaurants use case instance
     * @return response containing all available restaurants and their dishes
     */
    public BrowseRestaurantsResponse demonstrateBrowseAllRestaurants(BrowseRestaurantsUseCase useCase) {
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, null);
        return useCase.execute(request);
    }

    /**
     * Demonstrates filtering restaurants by availability status.
     * 
     * @param useCase the browse restaurants use case instance
     * @param openOnly true to show only open restaurants, false for closed only, null for all
     * @return response containing filtered restaurants
     */
    public BrowseRestaurantsResponse demonstrateBrowseByAvailability(BrowseRestaurantsUseCase useCase, Boolean openOnly) {
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, openOnly);
        return useCase.execute(request);
    }

    /**
     * Demonstrates filtering restaurants by dish categories.
     * 
     * @param useCase the browse restaurants use case instance
     * @param category the dish category to filter by (e.g., VEGETARIAN, MAIN_COURSE)
     * @return response containing restaurants that serve dishes of the specified category
     */
    public BrowseRestaurantsResponse demonstrateBrowseByCuisineType(BrowseRestaurantsUseCase useCase, DishCategory category) {
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(category, null);
        return useCase.execute(request);
    }

    /**
     * Demonstrates combining multiple filters for refined search results.
     * 
     * @param useCase the browse restaurants use case instance
     * @param category the dish category filter
     * @param openOnly the availability filter
     * @return response containing restaurants matching both criteria
     */
    public BrowseRestaurantsResponse demonstrateCombinedFilters(BrowseRestaurantsUseCase useCase, 
                                                               DishCategory category, 
                                                               Boolean openOnly) {
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(category, openOnly);
        return useCase.execute(request);
    }

    /**
     * Creates and configures the application context for demonstration purposes.
     * 
     * @return configured ApplicationConfig instance with all dependencies wired
     */
    public ApplicationConfig createApplicationContext() {
        return new ApplicationConfig();
    }
}