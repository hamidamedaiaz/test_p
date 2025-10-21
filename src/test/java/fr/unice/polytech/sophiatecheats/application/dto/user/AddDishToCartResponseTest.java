package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class AddDishToCartResponseTest {
    User user;
    Cart cart;

    @BeforeEach
    void setUp() {
        user = new User("name","email");
        cart = new Cart(user.getId());
    }

    @Test
    void should_throw_exception_when_arguments_are_illegal(){
        assertThrows(IllegalArgumentException.class, () -> new AddDishToCartResponse(null,0,new BigDecimal("0"),true));
    }

    @Test
    void should_set_price_to_zero_when_total_amount_is_null(){
        AddDishToCartResponse addDishToCartResponse = new AddDishToCartResponse(cart.getId(),0,null,true);
        assertEquals(BigDecimal.ZERO,addDishToCartResponse.totalAmount());
    }
}
