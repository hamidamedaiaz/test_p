package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.response.CancelCartResponse;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class CancelCartResponseTest {

    @Test
    void should_create_cancel_cart_response_with_valid_data() {
        UUID cartId = UUID.randomUUID();
        CancelCartResponse response = new CancelCartResponse(cartId, true, "Cart cancelled successfully");

        assertEquals(cartId, response.cancelledCartId());
        assertTrue(response.success());
        assertEquals("Cart cancelled successfully", response.message());
    }

    @Test
    void should_create_cancel_cart_response_with_null_cart_id() {
        CancelCartResponse response = new CancelCartResponse(null, false, "Cart not found");

        assertNull(response.cancelledCartId());
        assertFalse(response.success());
        assertEquals("Cart not found", response.message());
    }

    @Test
    void should_create_cancel_cart_response_with_failure() {
        UUID cartId = UUID.randomUUID();
        CancelCartResponse response = new CancelCartResponse(cartId, false, "Failed to cancel cart");

        assertEquals(cartId, response.cancelledCartId());
        assertFalse(response.success());
        assertEquals("Failed to cancel cart", response.message());
    }

    @Test
    void should_handle_null_message() {
        UUID cartId = UUID.randomUUID();
        CancelCartResponse response = new CancelCartResponse(cartId, true, null);

        assertEquals(cartId, response.cancelledCartId());
        assertTrue(response.success());
        assertNull(response.message());
    }
}

