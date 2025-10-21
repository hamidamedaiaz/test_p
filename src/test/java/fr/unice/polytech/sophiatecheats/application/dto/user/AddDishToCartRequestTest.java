package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertThrows;

class AddDishToCartRequestTest {
    User user;
    Dish dish;

    @BeforeEach
    void setUp() {
        user = new User("John", "Doe");
        Dish.Builder dishBuilder = new Dish.Builder();
        dishBuilder.name("name");
        dishBuilder.price(new BigDecimal("10"));
        dish = dishBuilder.build();
    }

    @Test
    void should_throw_exception_when_arguments_are_invalid(){
        assertThrows(IllegalArgumentException.class, () -> new AddDishToCartRequest(null,dish.getId(),2));
        assertThrows(IllegalArgumentException.class, () -> new AddDishToCartRequest(user.getId(),null,2));
        assertThrows(IllegalArgumentException.class, () -> new AddDishToCartRequest(user.getId(),dish.getId(),-1));
    }

}
