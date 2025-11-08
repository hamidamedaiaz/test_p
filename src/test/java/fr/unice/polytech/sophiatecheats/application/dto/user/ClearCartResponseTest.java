package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.response.ClearCartResponse;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class ClearCartResponseTest {

    @Test
    void should_create_clear_cart_response_with_valid_data() {
        UUID cartId = UUID.randomUUID();
        ClearCartResponse response = new ClearCartResponse(
            cartId,
            0,
            BigDecimal.ZERO,
            true,
            "Cart cleared successfully"
        );

        assertEquals(cartId, response.cartId());
        assertEquals(0, response.itemCount());
        assertEquals(BigDecimal.ZERO, response.totalAmount());
        assertTrue(response.success());
        assertEquals("Cart cleared successfully", response.message());
    }

    @Test
    void should_create_clear_cart_response_with_failure() {
        UUID cartId = UUID.randomUUID();
        ClearCartResponse response = new ClearCartResponse(
            cartId,
            5,
            new BigDecimal("50.00"),
            false,
            "Failed to clear cart"
        );

        assertEquals(cartId, response.cartId());
        assertEquals(5, response.itemCount());
        assertEquals(new BigDecimal("50.00"), response.totalAmount());
        assertFalse(response.success());
        assertEquals("Failed to clear cart", response.message());
    }

    @Test
    void should_handle_null_cart_id() {
        ClearCartResponse response = new ClearCartResponse(
            null,
            0,
            BigDecimal.ZERO,
            false,
            "Cart not found"
        );

        assertNull(response.cartId());
        assertFalse(response.success());
    }

    @Test
    void should_handle_null_total_amount() {
        UUID cartId = UUID.randomUUID();
        ClearCartResponse response = new ClearCartResponse(
            cartId,
            0,
            null,
            true,
            "Cart cleared"
        );

        assertNull(response.totalAmount());
    }
}

