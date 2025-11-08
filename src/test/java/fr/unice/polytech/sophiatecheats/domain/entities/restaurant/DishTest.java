package fr.unice.polytech.sophiatecheats.domain.entities.restaurant;

import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DishValidationException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DishTest {

    @Test
    void should_create_valid_dish() {
        // Given
        String name = "Pizza Margherita";
        String description = "Delicious pizza with tomato and mozzarella";
        BigDecimal price = new BigDecimal("12.50");
        DishCategory category = DishCategory.MAIN_COURSE;

        // When
        Dish dish = Dish.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(true)
                .build();

        // Then
        assertNotNull(dish.getId());
        assertEquals(name, dish.getName());
        assertEquals(description, dish.getDescription());
        assertEquals(price, dish.getPrice());
        assertEquals(category, dish.getCategory());
        assertTrue(dish.isAvailable());
    }

    @Test
    void should_fail_when_name_is_null() {
        // Given
        String name = null;
        String description = "Test description";
        BigDecimal price = new BigDecimal("10.00");
        DishCategory category = DishCategory.MAIN_COURSE;

        // When & Then
        assertThrows(DishValidationException.class, 
            () -> Dish.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(true)
                    .build());
    }

    @Test
    void should_fail_when_name_is_empty() {
        // Given
        String name = "   ";
        String description = "Test description";
        BigDecimal price = new BigDecimal("10.00");
        DishCategory category = DishCategory.MAIN_COURSE;

        // When & Then
        assertThrows(DishValidationException.class, 
            () -> Dish.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(true)
                    .build());
    }

    @Test
    void should_fail_when_price_is_negative() {
        // Given
        String name = "Test Dish";
        String description = "Test description";
        BigDecimal price = new BigDecimal("-1.00");
        DishCategory category = DishCategory.MAIN_COURSE;

        // When & Then
        assertThrows(DishValidationException.class, 
            () -> Dish.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(true)
                    .build());
    }

    @Test
    void should_fail_when_category_is_null() {
        // Given
        String name = "Test Dish";
        String description = "Test description";
        BigDecimal price = new BigDecimal("10.00");
        DishCategory category = null;

        // When & Then
        assertThrows(DishValidationException.class, 
            () -> Dish.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(true)
                    .build());
    }

    @Test
    void should_allow_zero_price() {
        // Given
        String name = "Free Sample";
        String description = "Free dish sample";
        BigDecimal price = BigDecimal.ZERO;
        DishCategory category = DishCategory.STARTER;

        // When & Then
        assertDoesNotThrow(() -> Dish.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(true)
                .build());
    }

    @Test
    void should_make_dish_available_and_unavailable() {
        // Given
        Dish dish = Dish.builder()
                .name("Test")
                .description("Test")
                .price(new BigDecimal("10"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();
        assertTrue(dish.isAvailable());

        // When
        dish.makeUnavailable();

        // Then
        assertFalse(dish.isAvailable());

        // When
        dish.makeAvailable();

        // Then
        assertTrue(dish.isAvailable());
    }

    @Test
    void should_have_consistent_equals_and_hashcode() {
        // Given
        UUID id = UUID.randomUUID();
        Dish dish1 = Dish.builder()
                .id(id)
                .name("Test")
                .description("Test")
                .price(new BigDecimal("10"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();
        Dish dish2 = Dish.builder()
                .id(id)
                .name("Different Name")
                .description("Different")
                .price(new BigDecimal("20"))
                .category(DishCategory.DESSERT)
                .available(false)
                .build();

        // Then
        assertEquals(dish1, dish2); // Same ID
        assertEquals(dish1.hashCode(), dish2.hashCode());
    }

    @Test
    void should_create_dish_with_full_constructor() {
        // Given
        UUID id = UUID.randomUUID();
        String name = "Pizza Romana";
        String description = "Pizza with olives";
        BigDecimal price = new BigDecimal("13.50");
        DishCategory category = DishCategory.MAIN_COURSE;
        boolean available = false;

        // When
        Dish dish = Dish.builder()
                .id(id)
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(available)
                .build();

        // Then
        assertEquals(id, dish.getId());
        assertEquals(name, dish.getName());
        assertEquals(description, dish.getDescription());
        assertEquals(price, dish.getPrice());
        assertEquals(category, dish.getCategory());
        assertFalse(dish.isAvailable());
    }

    @Test
    void should_have_meaningful_toString() {
        // Given
        Dish dish = Dish.builder()
                .name("Test Dish")
                .description("Description")
                .price(new BigDecimal("15.99"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build();

        // When
        String result = dish.toString();
        
        // Then
        assertNotNull(result);
        assertTrue(result.contains("Test Dish"));
        assertTrue(result.contains("15.99"));
        assertTrue(result.contains("DESSERT"));
        assertTrue(result.contains("available=true"));
    }

    @Test
    void should_allow_null_description() {
        // Given
        String name = "Test";
        String description = null;
        BigDecimal price = new BigDecimal("10.00");
        DishCategory category = DishCategory.MAIN_COURSE;

        // When & Then - Should not throw exception
        assertDoesNotThrow(() -> Dish.builder()
                .name(name)
                .description(description)
                .price(price)
                .category(category)
                .available(true)
                .build());
    }

    @Test
    void should_fail_validation_with_null_price() {
        // Given
        String name = "Test";
        String description = "Test desc";
        BigDecimal price = null;
        DishCategory category = DishCategory.MAIN_COURSE;

        // When & Then
        assertThrows(DishValidationException.class, 
            () -> Dish.builder()
                    .name(name)
                    .description(description)
                    .price(price)
                    .category(category)
                    .available(true)
                    .build());
    }

    @Test
    void should_not_equal_different_objects() {
        // Given
        Dish dish = Dish.builder()
                .name("Test")
                .description("Test")
                .price(new BigDecimal("10"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();

        // Then
        assertNotEquals(dish, null);
        assertNotEquals(dish, "not a dish");
        assertNotEquals(dish, Dish.builder()
                .name("Other")
                .description("Other")
                .price(new BigDecimal("20"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());
    }
}