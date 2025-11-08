package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.RemoveDishFromRestaurantRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;

 class RemoveDishFromRestaurantRequestTest {
    Restaurant restaurant;
    Dish dish;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("name","address");
        Dish.Builder dishBuilder = new Dish.Builder();
        dishBuilder.name("name");
        dishBuilder.price(new BigDecimal("10"));
        dish = dishBuilder.build();
    }

    @Test
    void should_throw_execption_when_arguments_are_invalid() {
        assertThrows(IllegalArgumentException.class, () -> new RemoveDishFromRestaurantRequest(null, UUID.randomUUID()));
        assertThrows(IllegalArgumentException.class, () -> new RemoveDishFromRestaurantRequest(restaurant.getId(), null));
    }
}
