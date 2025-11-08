package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.UpdateDishRequest;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

 class UpdateDishRequestTest {

    Restaurant restaurant;
    Dish dish;

    @BeforeEach
    void setUp() {
        restaurant = new Restaurant("name","address");
        Dish.Builder dishBuilder = new Dish.Builder();
        dishBuilder.name("name");
        dishBuilder.description("description");
        dishBuilder.price(new BigDecimal("10"));
        dishBuilder.category(DishCategory.MAIN_COURSE);
        dish = dishBuilder.build();
    }

    @Test
    void should_throw_exception_when_arguments_are_invalid(){
        assertThrows(IllegalArgumentException.class, () -> new UpdateDishRequest(null,
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                true)
        );
        assertThrows(IllegalArgumentException.class, () -> new UpdateDishRequest(restaurant.getId(),
                null,
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                true)
        );
        assertThrows(IllegalArgumentException.class, () -> new UpdateDishRequest(restaurant.getId(),
                dish.getId(),
                null,
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                true)
        );
        assertThrows(IllegalArgumentException.class, () -> new UpdateDishRequest(restaurant.getId(),
                dish.getId(),
                "   ",
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                true)
        );
        assertThrows(IllegalArgumentException.class, () -> new UpdateDishRequest(restaurant.getId(),
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                new BigDecimal("-10"),
                dish.getCategory(),
                true)
        );
    }
}
