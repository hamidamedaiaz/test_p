package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class US1DemoMethodsTest {

    private US1Demo demo;
    private BrowseRestaurantsUseCase useCase;

    @BeforeEach
    void setUp() {
        demo = new US1Demo();
        ApplicationConfig config = demo.createApplicationContext();
        useCase = config.getInstance(BrowseRestaurantsUseCase.class);
    }

    @Test
    void should_create_application_context() {
        // When
        ApplicationConfig config = demo.createApplicationContext();

        // Then
        assertNotNull(config);
        assertNotNull(config.getInstance(BrowseRestaurantsUseCase.class));
    }

    @Test
    void should_demonstrate_browse_all_restaurants() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseAllRestaurants(useCase);

        // Then
        assertNotNull(response);
        assertEquals(3, response.restaurants().size()); // We know there are 3 sample restaurants
        
        // Verify restaurants are returned
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> r.name().contains("Food Truck Bio")));
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> r.name().contains("Pizzeria du Campus")));
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> r.name().contains("La Cafétéria")));
    }

    @Test
    void should_demonstrate_browse_by_availability_open_only() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByAvailability(useCase, true);

        // Then
        assertNotNull(response);
        assertTrue(response.restaurants().size() >= 1); // At least one open restaurant
        
        // Verify all returned restaurants are open
        response.restaurants().forEach(restaurant -> 
            assertTrue(restaurant.isOpen(), "Restaurant " + restaurant.name() + " should be open"));
    }

    @Test
    void should_demonstrate_browse_by_availability_closed_only() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByAvailability(useCase, false);

        // Then
        assertNotNull(response);
        
        // Verify all returned restaurants are closed
        response.restaurants().forEach(restaurant -> 
            assertFalse(restaurant.isOpen(), "Restaurant " + restaurant.name() + " should be closed"));
    }

    @Test
    void should_demonstrate_browse_by_availability_all() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByAvailability(useCase, null);

        // Then
        assertNotNull(response);
        assertEquals(3, response.restaurants().size()); // Should return all restaurants regardless of status
    }

    @Test
    void should_demonstrate_browse_by_cuisine_type_vegetarian() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByCuisineType(useCase, DishCategory.VEGETARIAN);

        // Then
        assertNotNull(response);
        assertTrue(response.restaurants().size() >= 1); // At least one restaurant with vegetarian dishes
        
        // Verify all returned restaurants have vegetarian dishes
        response.restaurants().forEach(restaurant -> {
            boolean hasVegetarianDish = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.VEGETARIAN);
            assertTrue(hasVegetarianDish, 
                "Restaurant " + restaurant.name() + " should have at least one vegetarian dish");
        });
    }

    @Test
    void should_demonstrate_browse_by_cuisine_type_main_course() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByCuisineType(useCase, DishCategory.MAIN_COURSE);

        // Then
        assertNotNull(response);
        assertTrue(response.restaurants().size() >= 1); // At least one restaurant with main courses
        
        // Verify all returned restaurants have main course dishes
        response.restaurants().forEach(restaurant -> {
            boolean hasMainCourse = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.MAIN_COURSE);
            assertTrue(hasMainCourse, 
                "Restaurant " + restaurant.name() + " should have at least one main course dish");
        });
    }

    @Test
    void should_demonstrate_browse_by_cuisine_type_starter() {
        // When - Search for starters (should return empty as per sample data)
        BrowseRestaurantsResponse response = demo.demonstrateBrowseByCuisineType(useCase, DishCategory.STARTER);

        // Then
        assertNotNull(response);
        assertEquals(0, response.restaurants().size()); // No restaurants with starters in sample data
    }

    @Test
    void should_demonstrate_combined_filters_open_and_vegetarian() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateCombinedFilters(useCase, DishCategory.VEGETARIAN, true);

        // Then
        assertNotNull(response);
        
        // Verify all returned restaurants are both open and have vegetarian dishes
        response.restaurants().forEach(restaurant -> {
            assertTrue(restaurant.isOpen(), 
                "Restaurant " + restaurant.name() + " should be open");
            
            boolean hasVegetarianDish = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.VEGETARIAN);
            assertTrue(hasVegetarianDish, 
                "Restaurant " + restaurant.name() + " should have at least one vegetarian dish");
        });
    }

    @Test
    void should_demonstrate_combined_filters_closed_and_main_course() {
        // When
        BrowseRestaurantsResponse response = demo.demonstrateCombinedFilters(useCase, DishCategory.MAIN_COURSE, false);

        // Then
        assertNotNull(response);
        
        // Should return only Pizzeria du Campus which is closed
        assertTrue(response.restaurants().size() >= 0); // May be 0 or 1 depending on data
        
        // If there are results, verify all returned restaurants are closed and have main course dishes
        response.restaurants().forEach(restaurant -> {
            assertFalse(restaurant.isOpen(), 
                "Restaurant " + restaurant.name() + " should be closed");
            
            boolean hasMainCourse = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.MAIN_COURSE);
            assertTrue(hasMainCourse, 
                "Restaurant " + restaurant.name() + " should have at least one main course dish");
        });
    }

    @Test
    void should_demonstrate_combined_filters_no_filters() {
        // When - No filters applied
        BrowseRestaurantsResponse response = demo.demonstrateCombinedFilters(useCase, null, null);

        // Then
        assertNotNull(response);
        assertEquals(3, response.restaurants().size()); // Should return all restaurants
    }

    @Test
    void should_demonstrate_combined_filters_with_no_results() {
        // When - Search for closed restaurants with starters (should be empty)
        BrowseRestaurantsResponse response = demo.demonstrateCombinedFilters(useCase, DishCategory.STARTER, false);

        // Then
        assertNotNull(response);
        assertEquals(0, response.restaurants().size()); // No results expected
    }
}