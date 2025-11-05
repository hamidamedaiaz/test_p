package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BrowseRestaurantsUseCaseTest {

    private BrowseRestaurantsUseCase useCase;
    private ApplicationConfig config;

    @BeforeEach
    void setUp() {
        config = new ApplicationConfig();
        useCase = config.getInstance(BrowseRestaurantsUseCase.class);
    }

    @Test
    void should_return_all_restaurants_when_no_filters() {
        // Given
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, null);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertNotNull(response.restaurants());
        assertEquals(3, response.restaurants().size()); // Sample data has 3 restaurants
        
        // Verify restaurant names from sample data
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> "La Cafétéria".equals(r.name())));
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> "Food Truck Bio".equals(r.name())));
        assertTrue(response.restaurants().stream()
            .anyMatch(r -> "Pizzeria du Campus".equals(r.name())));
    }

    @Test
    void should_filter_by_availability_open_only() {
        // Given
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, true);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertEquals(2, response.restaurants().size()); // Only open restaurants
        
        // Pizzeria is closed in sample data
        assertFalse(response.restaurants().stream()
            .anyMatch(r -> "Pizzeria du Campus".equals(r.name())));
        
        // Verify all returned restaurants are open
        response.restaurants().forEach(restaurant -> 
            assertTrue(restaurant.isOpen()));
    }

    @Test
    void should_filter_by_dish_category() {
        // Given
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(DishCategory.VEGETARIAN, null);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        
        // Only Food Truck Bio has vegetarian dishes in sample data
        assertEquals(1, response.restaurants().size());
        assertEquals("Food Truck Bio", response.restaurants().get(0).name());
    }

    @Test
    void should_filter_by_both_availability_and_category() {
        // Given - Look for open restaurants with main courses
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(DishCategory.MAIN_COURSE, true);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        
        // Should return open restaurants that serve main courses
        assertTrue(response.restaurants().size() >= 1);
        response.restaurants().forEach(restaurant -> {
            assertTrue(restaurant.isOpen());
            assertTrue(restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.MAIN_COURSE));
        });
    }

    @Test
    void should_return_empty_list_when_no_matches() {
        // Given - Look for a category that doesn't exist in sample data
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(DishCategory.STARTER, null);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        assertTrue(response.restaurants().isEmpty());
    }

    @Test
    void should_handle_null_request() {
        // When
        BrowseRestaurantsResponse response = useCase.execute(null);

        // Then
        assertNotNull(response);
        assertTrue(response.restaurants().isEmpty());
    }

    @Test
    void should_return_only_available_dishes_in_response() {
        // Given
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, null);

        // When
        BrowseRestaurantsResponse response = useCase.execute(request);

        // Then
        assertNotNull(response);
        
        // Verify all dishes in the response are available
        response.restaurants().forEach(restaurant -> 
            restaurant.dishes().forEach(dish -> 
                assertTrue(dish.available())));
    }
}