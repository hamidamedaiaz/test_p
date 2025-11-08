package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

 class PlaceOrderRequestTest {

    @Test
    void should_create_valid_place_order_request() {
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        assertEquals(userId, request.userId());
        assertEquals(restaurantId, request.restaurantId());
        assertEquals(PaymentMethod.STUDENT_CREDIT, request.paymentMethod());
        assertTrue(request.isValid());
    }

    @Test
    void should_validate_request_with_external_card() {
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            PaymentMethod.EXTERNAL_CARD
        );

        assertTrue(request.isValid());
        assertEquals(PaymentMethod.EXTERNAL_CARD, request.paymentMethod());
    }

    @Test
    void should_be_invalid_when_user_id_is_null() {
        UUID restaurantId = UUID.randomUUID();
        PlaceOrderRequest request = new PlaceOrderRequest(
            null,
            restaurantId,
            PaymentMethod.STUDENT_CREDIT
        );

        assertFalse(request.isValid());
    }

    @Test
    void should_be_invalid_when_restaurant_id_is_null() {
        UUID userId = UUID.randomUUID();
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            null,
            PaymentMethod.STUDENT_CREDIT
        );

        assertFalse(request.isValid());
    }

    @Test
    void should_be_invalid_when_payment_method_is_null() {
        UUID userId = UUID.randomUUID();
        UUID restaurantId = UUID.randomUUID();
        PlaceOrderRequest request = new PlaceOrderRequest(
            userId,
            restaurantId,
            null
        );

        assertFalse(request.isValid());
    }

    @Test
    void should_be_invalid_when_all_fields_are_null() {
        PlaceOrderRequest request = new PlaceOrderRequest(null, null, null);

        assertFalse(request.isValid());
    }
}

