package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.DishDto;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class RestaurantDtoTest {

    @Test
    void should_validate_valid_restaurant_dto() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertTrue(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_id() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            null, // null ID
            "Test Restaurant",
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_name() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            null, // null name
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_empty_name() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "   ", // empty name
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_address() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            null, // null address
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_empty_address() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            "", // empty address
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_dishes_list() {
        // Given
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            null // null dishes list
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_validate_with_null_opening_times() {
        // Given - Restaurant without specific opening hours
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "24/7 Restaurant",
            "123 Always Open Street",
            null, // null opening time
            null, // null closing time
            true,
            List.of()
        );

        // When & Then
        assertTrue(dto.isValid()); // Should still be valid
    }

    @Test
    void should_validate_with_dishes() {
        // Given
        DishDto dish = new DishDto(
            UUID.randomUUID(),
            "Test Dish",
            "Description",
            new BigDecimal("10.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );
        
        RestaurantDto dto = new RestaurantDto(
            UUID.randomUUID(),
            "Test Restaurant",
            "123 Test Street",
            LocalTime.of(9, 0),
            LocalTime.of(18, 0),
            true,
            List.of(dish)
        );

        // When & Then
        assertTrue(dto.isValid());
    }
}