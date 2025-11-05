package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class CartTest {
    private Cart cart;
    private UUID userId;
    private Dish dish1;
    private Dish dish2;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        cart = new Cart(userId);

        // Création de plats pour les tests
        dish1 = new Dish("Pizza", "Description 1", BigDecimal.valueOf(10.0), DishCategory.MAIN_COURSE);
        dish2 = new Dish("Pasta", "Description 2", BigDecimal.valueOf(8.0), DishCategory.MAIN_COURSE);
    }

    @Test
    void should_create_empty_cart() {
        assertNotNull(cart.getId());
        assertEquals(userId, cart.getUserId());
        assertTrue(cart.getItems().isEmpty());
        assertEquals(BigDecimal.ZERO, cart.calculateTotal());
        assertEquals(0, cart.getTotalItems());
        assertTrue(cart.isEmpty());
    }

    @Test
    void should_throw_exception_when_creating_cart_with_null_userId() {
        assertThrows(ValidationException.class, () -> new Cart(null));
    }

    @Test
    void should_add_dish_to_cart() {
        cart.addDish(dish1, 2);

        assertEquals(1, cart.getItems().size());
        assertEquals(2, cart.getTotalItems());
        assertEquals(BigDecimal.valueOf(20.0), cart.calculateTotal());
        assertFalse(cart.isEmpty());
    }

    @Test
    void should_update_quantity_when_adding_existing_dish() {
        cart.addDish(dish1, 2);
        cart.addDish(dish1, 3);

        assertEquals(1, cart.getItems().size());
        assertEquals(5, cart.getTotalItems());
        assertEquals(BigDecimal.valueOf(50.0), cart.calculateTotal());
    }

    @Test
    void should_add_multiple_different_dishes() {
        cart.addDish(dish1, 2);
        cart.addDish(dish2, 1);

        assertEquals(2, cart.getItems().size());
        assertEquals(3, cart.getTotalItems());
        assertEquals(BigDecimal.valueOf(28.0), cart.calculateTotal());
    }

    @Test
    void should_update_dish_quantity() {
        cart.addDish(dish1, 2);
        cart.updateQuantity(dish1.getId(), 4);

        assertEquals(1, cart.getItems().size());
        assertEquals(4, cart.getTotalItems());
        assertEquals(BigDecimal.valueOf(40.0), cart.calculateTotal());
    }

    @Test
    void should_remove_dish_when_updating_quantity_to_zero() {
        cart.addDish(dish1, 2);
        cart.updateQuantity(dish1.getId(), 0);

        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalItems());
        assertEquals(BigDecimal.ZERO, cart.calculateTotal());
    }

    @Test
    void should_remove_dish() {
        cart.addDish(dish1, 2);
        cart.addDish(dish2, 1);
        cart.removeDish(dish1.getId());

        assertEquals(1, cart.getItems().size());
        assertEquals(1, cart.getTotalItems());
        assertEquals(BigDecimal.valueOf(8.0), cart.calculateTotal());
    }

    @Test
    void should_clear_cart() {
        cart.addDish(dish1, 2);
        cart.addDish(dish2, 1);
        cart.clear();

        assertTrue(cart.isEmpty());
        assertEquals(0, cart.getTotalItems());
        assertEquals(BigDecimal.ZERO, cart.calculateTotal());
    }

    @Test
    void should_throw_exception_when_adding_null_dish() {
        assertThrows(ValidationException.class, () -> cart.addDish(null, 1));
    }

    @Test
    void should_throw_exception_when_adding_unavailable_dish() {
        dish1.makeUnavailable();
        assertThrows(ValidationException.class, () -> cart.addDish(dish1, 1));
    }

    @Test
    void should_throw_exception_when_adding_negative_quantity() {
        assertThrows(ValidationException.class, () -> cart.addDish(dish1, -1));
    }

    @Test
    void should_throw_exception_when_adding_zero_quantity() {
        assertThrows(ValidationException.class, () -> cart.addDish(dish1, 0));
    }

    @Test
    void should_throw_exception_when_exceeding_max_quantity() {
        assertThrows(ValidationException.class, () -> cart.addDish(dish1, 11));
    }

    @Test
    void should_throw_exception_when_updating_nonexistent_dish() {
        assertThrows(ValidationException.class, () -> cart.updateQuantity(UUID.randomUUID(), 1));
    }

    @Test
    void should_throw_exception_when_updating_with_invalid_quantity() {
        cart.addDish(dish1, 1);
        assertThrows(ValidationException.class, () -> cart.updateQuantity(dish1.getId(), 11));
    }

    @Test
    void should_return_immutable_item_list() {
        cart.addDish(dish1, 1);
        assertThrows(UnsupportedOperationException.class, () -> cart.getItems().add(new CartItem(dish2, 1)));
    }
}
