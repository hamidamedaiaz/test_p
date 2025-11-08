package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.request.ConfirmOrderRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

 class ConfirmOrderRequestTest {

    @Test
    void should_create_valid_confirm_order_request() {
        ConfirmOrderRequest request = new ConfirmOrderRequest("ORDER-123");

        assertEquals("ORDER-123", request.orderId());
        assertTrue(request.isValid());
    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings = {"   ", "\t", "\n"})
    void should_be_invalid_when_order_id_is_null_empty_or_blank(String orderId) {
        ConfirmOrderRequest request = new ConfirmOrderRequest(orderId);

        assertFalse(request.isValid());
    }

    @Test
    void should_be_valid_with_complex_order_id() {
        ConfirmOrderRequest request = new ConfirmOrderRequest("ORD-2024-12345-ABC");

        assertTrue(request.isValid());
        assertEquals("ORD-2024-12345-ABC", request.orderId());
    }
}

