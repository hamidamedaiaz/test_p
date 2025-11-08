package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.RemoveDishFromRestaurantResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class RemoveDishFromRestaurantResponseTest {

    @Test
    void should_create_remove_dish_response_with_success() {
        UUID dishId = UUID.randomUUID();
        RemoveDishFromRestaurantResponse response = new RemoveDishFromRestaurantResponse(
            dishId,
            "Dish removed successfully",
            true
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Dish removed successfully", response.message());
        assertTrue(response.success());
    }

    @Test
    void should_create_remove_dish_response_with_failure() {
        UUID dishId = UUID.randomUUID();
        RemoveDishFromRestaurantResponse response = new RemoveDishFromRestaurantResponse(
            dishId,
            "Failed to remove dish",
            false
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Failed to remove dish", response.message());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_dish_id() {
        RemoveDishFromRestaurantResponse response = new RemoveDishFromRestaurantResponse(
            null,
            "Dish not found",
            false
        );
        
        assertNull(response.dishId());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_message() {
        UUID dishId = UUID.randomUUID();
        RemoveDishFromRestaurantResponse response = new RemoveDishFromRestaurantResponse(
            dishId,
            null,
            true
        );
        
        assertNull(response.message());
        assertTrue(response.success());
    }

    @Test
    void should_create_response_with_dish_not_in_restaurant_error() {
        UUID dishId = UUID.randomUUID();
        RemoveDishFromRestaurantResponse response = new RemoveDishFromRestaurantResponse(
            dishId,
            "Dish not found in restaurant",
            false
        );
        
        assertEquals("Dish not found in restaurant", response.message());
        assertFalse(response.success());
    }
}

