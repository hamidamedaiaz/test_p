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
        Dish dish = new Dish(UUID.randomUUID(), name, description, price, category, true);

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
            () -> new Dish(UUID.randomUUID(), name, description, price, category, true));
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
            () -> new Dish(UUID.randomUUID(), name, description, price, category, true));
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
            () -> new Dish(UUID.randomUUID(), name, description, price, category, true));
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
            () -> new Dish(UUID.randomUUID(), name, description, price, category, true));
    }

    @Test
    void should_allow_zero_price() {
        // Given
        String name = "Free Sample";
        String description = "Free dish sample";
        BigDecimal price = BigDecimal.ZERO;
        DishCategory category = DishCategory.STARTER;

        // When & Then
        assertDoesNotThrow(() -> new Dish(UUID.randomUUID(), name, description, price, category, true));
    }

    @Test
    void should_make_dish_available_and_unavailable() {
        // Given
        Dish dish = new Dish(UUID.randomUUID(), "Test", "Test", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
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
        Dish dish1 = new Dish(id, "Test", "Test", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        Dish dish2 = new Dish(id, "Different Name", "Different", new BigDecimal("20"), DishCategory.DESSERT, false);

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
        Dish dish = new Dish(id, name, description, price, category, available);

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
        Dish dish = new Dish(UUID.randomUUID(), "Test Dish", "Description", new BigDecimal("15.99"), DishCategory.DESSERT, true);
        
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
        assertDoesNotThrow(() -> new Dish(UUID.randomUUID(), name, description, price, category, true));
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
            () -> new Dish(UUID.randomUUID(), name, description, price, category, true));
    }

    @Test
    void should_not_equal_different_objects() {
        // Given
        Dish dish = new Dish(UUID.randomUUID(), "Test", "Test", new BigDecimal("10"), DishCategory.MAIN_COURSE, true);
        
        // Then
        assertNotEquals(dish, null);
        assertNotEquals(dish, "not a dish");
        assertNotEquals(dish, new Dish(UUID.randomUUID(), "Other", "Other", new BigDecimal("20"), DishCategory.DESSERT, true));
    }
}