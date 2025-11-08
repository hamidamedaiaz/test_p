package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.AddDishToRestaurantRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

 class AddDishToRestaurantRequestTest {

    Restaurant restaurant;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("name","address");
    }

    @Test
    void shouldThrowExceptionWhenArgumentsAreInvalid() {
        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                null,
                "Pizza",
                "Description",
                new BigDecimal("10.00"),
                DishCategory.MAIN_COURSE,
                true
        ));

        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                restaurant.getId(),
                null,
                "Description",
                new BigDecimal("10.00"),
                DishCategory.MAIN_COURSE,
                true
        ));

        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                restaurant.getId(),
                "   ",
                "Description",
                new BigDecimal("10.00"),
                DishCategory.MAIN_COURSE,
                true
        ));

        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                restaurant.getId(),
                "Pizza",
                "Description",
                null,
                DishCategory.MAIN_COURSE,
                true
        ));

        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                restaurant.getId(),
                "Pizza",
                "Description",
                new BigDecimal("-10.00"),
                DishCategory.MAIN_COURSE,
                true
        ));

        assertThrows(IllegalArgumentException.class, () -> new AddDishToRestaurantRequest(
                restaurant.getId(),
                "Pizza",
                "Description",
                new BigDecimal("10.00"),
                null,
                true
        ));
    }
}
