package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.DishDto;
import fr.unice.polytech.sophiatecheats.application.dto.user.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class BrowseRestaurantsResponseTest {

    @Test
    void should_validate_valid_response() {
        // Given
        RestaurantDto restaurant = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            "123 Test Street",
            null,
            null,
            true,
            List.of()
        );
        
        BrowseRestaurantsResponse response = new BrowseRestaurantsResponse(
            List.of(restaurant)
        );

        // When & Then
        assertTrue(response.isValid());
    }

    @Test
    void should_validate_empty_response() {
        // Given
        BrowseRestaurantsResponse response = new BrowseRestaurantsResponse(
            List.of() // empty list
        );

        // When & Then
        assertTrue(response.isValid());
    }

    @Test
    void should_fail_validation_with_null_list() {
        // Given
        BrowseRestaurantsResponse response = new BrowseRestaurantsResponse(
            null // null list
        );

        // When & Then
        assertFalse(response.isValid());
    }

    @Test
    void should_validate_multiple_restaurants() {
        // Given
        RestaurantDto restaurant1 = new RestaurantDto(
            UUID.randomUUID(),
            "Restaurant 1",
            "Address 1",
            null,
            null,
            true,
            List.of()
        );
        
        RestaurantDto restaurant2 = new RestaurantDto(
            UUID.randomUUID(),
            "Restaurant 2",
            "Address 2",
            null,
            null,
            false,
            List.of()
        );
        
        BrowseRestaurantsResponse response = new BrowseRestaurantsResponse(
            List.of(restaurant1, restaurant2)
        );

        // When & Then
        assertTrue(response.isValid());
        assertEquals(2, response.restaurants().size());
    }

    @Test
    void should_validate_response_with_restaurants_containing_dishes() {
        // Given
        DishDto dish = new DishDto(
            UUID.randomUUID(),
            "Test Dish",
            "Description",
            new BigDecimal("10.00"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );
        
        RestaurantDto restaurant = new RestaurantDto(
            UUID.randomUUID(),
            "Restaurant with Dishes",
            "Address",
            null,
            null,
            true,
            List.of(dish)
        );
        
        BrowseRestaurantsResponse response = new BrowseRestaurantsResponse(
            List.of(restaurant)
        );

        // When & Then
        assertTrue(response.isValid());
        assertEquals(1, response.restaurants().size());
        assertEquals(1, response.restaurants().get(0).dishes().size());
    }
}