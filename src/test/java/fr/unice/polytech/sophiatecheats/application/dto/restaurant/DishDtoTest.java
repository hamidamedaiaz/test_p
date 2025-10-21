package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.DishDto;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishDtoTest {

    @Test
    void should_validate_valid_dish_dto() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            "Delicious pizza",
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertTrue(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_id() {
        // Given
        DishDto dto = new DishDto(
            null, // null ID
            "Pizza Margherita",
            "Delicious pizza",
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_name() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            null, // null name
            "Delicious pizza",
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_empty_name() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "   ", // empty name
            "Delicious pizza",
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_description() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            null, // null description
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_empty_description() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            "   ", // empty/whitespace description
            new BigDecimal("12.50"),
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_price() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            "Delicious pizza",
            null, // null price
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_fail_validation_with_negative_price() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            "Delicious pizza",
            new BigDecimal("-5.00"), // negative price
            DishCategory.MAIN_COURSE,
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_validate_with_zero_price() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Free Sample",
            "Free sample dish",
            BigDecimal.ZERO, // zero price
            DishCategory.STARTER,
            true,
            new HashSet<>()
        );

        // When & Then
        assertTrue(dto.isValid());
    }

    @Test
    void should_fail_validation_with_null_category() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Pizza Margherita",
            "Delicious pizza",
            new BigDecimal("12.50"),
            null, // null category
            true,
            new HashSet<>()
        );

        // When & Then
        assertFalse(dto.isValid());
    }

    @Test
    void should_validate_unavailable_dish() {
        // Given
        DishDto dto = new DishDto(
            UUID.randomUUID(),
            "Out of Stock Dish",
            "Currently unavailable",
            new BigDecimal("15.00"),
            DishCategory.DESSERT,
            false, // unavailable
            new HashSet<>()
        );

        // When & Then
        assertTrue(dto.isValid());
    }

    @Test
    void should_validate_all_dish_categories() {
        // Test all enum values
        for (DishCategory category : DishCategory.values()) {
            // Given
            DishDto dto = new DishDto(
                UUID.randomUUID(),
                "Test Dish",
                "Test description",
                new BigDecimal("10.00"),
                category,
                true,
                new HashSet<>()
            );

            // When & Then
            assertTrue(dto.isValid(), "Should validate for category: " + category);
        }
    }
}