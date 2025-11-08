package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.UpdateDishResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class UpdateDishResponseTest {

    @Test
    void should_create_update_dish_response_with_success() {
        UUID dishId = UUID.randomUUID();
        UpdateDishResponse response = new UpdateDishResponse(
            dishId,
            "Dish updated successfully",
            true
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Dish updated successfully", response.message());
        assertTrue(response.success());
    }

    @Test
    void should_create_update_dish_response_with_failure() {
        UUID dishId = UUID.randomUUID();
        UpdateDishResponse response = new UpdateDishResponse(
            dishId,
            "Failed to update dish",
            false
        );
        
        assertEquals(dishId, response.dishId());
        assertEquals("Failed to update dish", response.message());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_dish_id() {
        UpdateDishResponse response = new UpdateDishResponse(
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
        UpdateDishResponse response = new UpdateDishResponse(
            dishId,
            null,
            true
        );
        
        assertNull(response.message());
        assertTrue(response.success());
    }
}

