package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CartDetailResponseTest {
    User user;
    Cart cart;
    CartItemDto cartItem;
    List<CartItemDto> cartItems;

    @BeforeEach
    void setUp() {
        user = new User("John Doe", "john@doe.com");
        cart = new Cart(user.getId());
        cartItem = new CartItemDto(UUID.randomUUID(),"name","description",new BigDecimal("10"),2,new BigDecimal("20"));
        cartItems = new ArrayList<>();
        cartItems.add(cartItem);
    }

    @Test
    void should_throw_exception_when_arguments_are_invalid(){
        assertThrows(IllegalArgumentException.class, () -> new CartDetailsResponse(
                null,
                user.getId(),
                cartItems,
                new BigDecimal("20"),
                2,
                false
        ));

        assertThrows(IllegalArgumentException.class, () -> new CartDetailsResponse(
                cart.getId(),
                null,
                cartItems,
                new BigDecimal("20"),
                2,
                false
        ));

        assertThrows(IllegalArgumentException.class, () -> new CartDetailsResponse(
                cart.getId(),
                user.getId(),
                null,
                new BigDecimal("20"),
                2,
                false
        ));
    }

    @Test
    void should_set_price_to_zero_if_total_amount_is_null(){
        CartDetailsResponse cartDetailsResponse = new CartDetailsResponse(
                cart.getId(),
                user.getId(),
                cartItems,
                null,
                2,
                false
        );

        assertEquals(BigDecimal.ZERO,cartDetailsResponse.totalAmount());
    }
}
