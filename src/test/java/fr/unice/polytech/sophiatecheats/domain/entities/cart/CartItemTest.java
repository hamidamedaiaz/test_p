package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("CartItem Entity Tests")
class CartItemTest {

    private Dish testDish;

    @BeforeEach
    void setUp() {
        testDish = Dish.builder()
            .name("Pizza Margherita")
            .description("Traditional Italian pizza")
            .price(new BigDecimal("12.50"))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();
    }

    @Test
    @DisplayName("Should create cart item with valid dish and quantity")
    void shouldCreateCartItemWithValidDishAndQuantity() {
        CartItem cartItem = new CartItem(testDish, 2);

        assertEquals(testDish.getId(), cartItem.getDishId());
        assertEquals(testDish.getName(), cartItem.getDishName());
        assertEquals(testDish.getDescription(), cartItem.getDishDescription());
        assertEquals(testDish.getPrice(), cartItem.getUnitPrice());
        assertEquals(2, cartItem.getQuantity());
        assertEquals(new BigDecimal("25.00"), cartItem.getSubtotal());
    }

    @Test
    @DisplayName("Should throw exception when creating cart item with null dish")
    void shouldThrowExceptionWhenCreatingCartItemWithNullDish() {
        assertThrows(ValidationException.class, () -> new CartItem(null, 1));
    }

    @Test
    @DisplayName("Should throw exception when creating cart item with invalid quantity")
    void shouldThrowExceptionWhenCreatingCartItemWithInvalidQuantity() {
        assertThrows(ValidationException.class, () -> new CartItem(testDish, 0));
        assertThrows(ValidationException.class, () -> new CartItem(testDish, -1));
    }

    @Test
    @DisplayName("Should update quantity correctly")
    void shouldUpdateQuantityCorrectly() {
        CartItem cartItem = new CartItem(testDish, 2);
        cartItem.updateQuantity(5);

        assertEquals(5, cartItem.getQuantity());
        assertEquals(new BigDecimal("62.50"), cartItem.getSubtotal());
    }

    @Test
    @DisplayName("Should throw exception when updating to invalid quantity")
    void shouldThrowExceptionWhenUpdatingToInvalidQuantity() {
        CartItem cartItem = new CartItem(testDish, 2);

        assertThrows(ValidationException.class, () -> cartItem.updateQuantity(0));
        assertThrows(ValidationException.class, () -> cartItem.updateQuantity(-1));
    }

    @Test
    @DisplayName("Should calculate subtotal correctly")
    void shouldCalculateSubtotalCorrectly() {
        CartItem cartItem = new CartItem(testDish, 3);
        assertEquals(new BigDecimal("37.50"), cartItem.getSubtotal());

        cartItem.updateQuantity(1);
        assertEquals(new BigDecimal("12.50"), cartItem.getSubtotal());
    }

}
