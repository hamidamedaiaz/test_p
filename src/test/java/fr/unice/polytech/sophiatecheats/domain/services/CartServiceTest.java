package fr.unice.polytech.sophiatecheats.domain.services;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CartServiceTest {

    private CartRepository cartRepo;
    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartRepo = Mockito.mock(CartRepository.class);
        cartService = new CartService(cartRepo);
    }

    @Test
    void testAddToCartAndGetItems() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());
        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));

        Dish dish = Dish.builder().name("Pizza").description("Delicious").price(BigDecimal.valueOf(10)).build();
        cartService.addToCart(user, dish, 2);

        List<CartItem> items = cartService.getItems(user);
        assertEquals(1, items.size());
        assertEquals("Pizza", items.getFirst().getDishName());
        assertEquals(2, items.getFirst().getQuantity());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    void testRemoveFromCart() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());
        Dish dish = Dish.builder().name("Burger").description("Yummy").price(BigDecimal.valueOf(5)).build();
        cart.addDish(dish, 1);

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));

        cartService.removeFromCart(user, dish.getId());
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    void testClearCart() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());
        Dish dish = Dish.builder().name("Burger").description("Yummy").price(BigDecimal.valueOf(5)).build();
        cart.addDish(dish, 3);

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));

        cartService.clearCart(user);
        assertTrue(cart.getItems().isEmpty());
        verify(cartRepo, times(1)).save(cart);
    }

    @Test
    void testCalculateTotal() {
        User user = new User("u@mail.com", "User");
        Cart cart = new Cart(user.getId());

        Dish dish1 = Dish.builder().name("Pizza").description("Delicious").price(BigDecimal.valueOf(10)).build();
        Dish dish2 = Dish.builder().name("Burger").description("Yummy").price(BigDecimal.valueOf(5)).build();
        cart.addDish(dish1, 2);
        cart.addDish(dish2, 1);

        when(cartRepo.findActiveCartByUserId(user.getId())).thenReturn(Optional.of(cart));

        BigDecimal total = cartService.calculateTotal(user);
        assertEquals(BigDecimal.valueOf(25), total);
    }
}
