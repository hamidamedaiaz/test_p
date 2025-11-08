package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

 class PlaceOrderResponseTest {

    @Test
    void should_create_place_order_response_with_valid_data() {
        LocalDateTime orderTime = LocalDateTime.now();
        PlaceOrderResponse response = new PlaceOrderResponse(
            "ORDER-123",
            "John Doe",
            "Restaurant A",
            new BigDecimal("50.00"),
            OrderStatus.PENDING,
            PaymentMethod.STUDENT_CREDIT,
            orderTime
        );

        assertEquals("ORDER-123", response.orderId());
        assertEquals("John Doe", response.customerName());
        assertEquals("Restaurant A", response.restaurantName());
        assertEquals(new BigDecimal("50.00"), response.totalAmount());
        assertEquals(OrderStatus.PENDING, response.status());
        assertEquals(PaymentMethod.STUDENT_CREDIT, response.paymentMethod());
        assertEquals(orderTime, response.orderDateTime());
    }

    @Test
    void should_create_response_with_external_card_payment() {
        LocalDateTime orderTime = LocalDateTime.now();
        PlaceOrderResponse response = new PlaceOrderResponse(
            "ORDER-456",
            "Jane Smith",
            "Restaurant B",
            new BigDecimal("75.50"),
            OrderStatus.CONFIRMED,
            PaymentMethod.EXTERNAL_CARD,
            orderTime
        );

        assertEquals(PaymentMethod.EXTERNAL_CARD, response.paymentMethod());
        assertEquals(OrderStatus.CONFIRMED, response.status());
    }

    @Test
    void should_handle_different_order_statuses() {
        LocalDateTime orderTime = LocalDateTime.now();
        PlaceOrderResponse response = new PlaceOrderResponse(
            "ORDER-789",
            "Bob Johnson",
            "Restaurant C",
            new BigDecimal("30.00"),
            OrderStatus.PREPARING,
            PaymentMethod.STUDENT_CREDIT,
            orderTime
        );

        assertEquals(OrderStatus.PREPARING, response.status());
    }

    @Test
    void should_handle_null_values() {
        PlaceOrderResponse response = new PlaceOrderResponse(
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        assertNull(response.orderId());
        assertNull(response.customerName());
        assertNull(response.restaurantName());
        assertNull(response.totalAmount());
        assertNull(response.status());
        assertNull(response.paymentMethod());
        assertNull(response.orderDateTime());
    }

    @Test
    void should_handle_zero_total_amount() {
        LocalDateTime orderTime = LocalDateTime.now();
        PlaceOrderResponse response = new PlaceOrderResponse(
            "ORDER-000",
            "Test User",
            "Test Restaurant",
            BigDecimal.ZERO,
            OrderStatus.PENDING,
            PaymentMethod.STUDENT_CREDIT,
            orderTime
        );

        assertEquals(BigDecimal.ZERO, response.totalAmount());
    }
}

