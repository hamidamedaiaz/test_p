package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.AddDishToRestaurantResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class AddDishToRestaurantResponseTest {

    @Test
    void should_create_add_dish_response_with_success() {
        UUID dishId = UUID.randomUUID();
        AddDishToRestaurantResponse response = new AddDishToRestaurantResponse(
            dishId,
            "Dish added successfully",
            true
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Dish added successfully", response.message());
        assertTrue(response.success());
    }

    @Test
    void should_create_add_dish_response_with_failure() {
        UUID dishId = UUID.randomUUID();
        AddDishToRestaurantResponse response = new AddDishToRestaurantResponse(
            dishId,
            "Failed to add dish",
            false
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Failed to add dish", response.message());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_dish_id() {
        AddDishToRestaurantResponse response = new AddDishToRestaurantResponse(
            null,
            "Restaurant not found",
            false
        );
        
        assertNull(response.dishId());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_message() {
        UUID dishId = UUID.randomUUID();
        AddDishToRestaurantResponse response = new AddDishToRestaurantResponse(
            dishId,
            null,
            true
        );
        
        assertNull(response.message());
        assertTrue(response.success());
    }

    @Test
    void should_create_response_with_empty_message() {
        UUID dishId = UUID.randomUUID();
        AddDishToRestaurantResponse response = new AddDishToRestaurantResponse(
            dishId,
            "",
            true
        );
        
        assertEquals("", response.message());
        assertTrue(response.success());
    }
}

