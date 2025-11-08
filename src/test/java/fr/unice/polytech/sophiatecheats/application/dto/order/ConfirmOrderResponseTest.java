package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.order.response.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

 class ConfirmOrderResponseTest {

    @Test
    void should_create_confirm_order_response_with_valid_data() {
        LocalDateTime confirmedAt = LocalDateTime.now();
        LocalDateTime deliveryTime = confirmedAt.plusHours(2);
        
        ConfirmOrderResponse response = new ConfirmOrderResponse(
            "ORDER-123",
            "John Doe",
            "Restaurant A",
            new BigDecimal("45.00"),
            OrderStatus.CONFIRMED,
            confirmedAt,
            deliveryTime
        );
        
        assertEquals("ORDER-123", response.orderId());
        assertEquals("John Doe", response.customerName());
        assertEquals("Restaurant A", response.restaurantName());
        assertEquals(new BigDecimal("45.00"), response.totalAmount());
        assertEquals(OrderStatus.CONFIRMED, response.status());
        assertEquals(confirmedAt, response.confirmedAt());
        assertEquals(deliveryTime, response.deliveryTime());
    }

    @Test
    void should_create_response_with_preparing_status() {
        LocalDateTime confirmedAt = LocalDateTime.now();
        LocalDateTime deliveryTime = confirmedAt.plusMinutes(45);
        
        ConfirmOrderResponse response = new ConfirmOrderResponse(
            "ORDER-456",
            "Jane Smith",
            "Restaurant B",
            new BigDecimal("30.50"),
            OrderStatus.PREPARING,
            confirmedAt,
            deliveryTime
        );
        
        assertEquals(OrderStatus.PREPARING, response.status());
    }

    @Test
    void should_handle_null_values() {
        ConfirmOrderResponse response = new ConfirmOrderResponse(
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
        assertNull(response.confirmedAt());
        assertNull(response.deliveryTime());
    }

    @Test
    void should_handle_different_delivery_times() {
        LocalDateTime confirmedAt = LocalDateTime.now();
        LocalDateTime deliveryTime = confirmedAt.plusDays(1);
        
        ConfirmOrderResponse response = new ConfirmOrderResponse(
            "ORDER-789",
            "Bob Johnson",
            "Restaurant C",
            new BigDecimal("75.00"),
            OrderStatus.CONFIRMED,
            confirmedAt,
            deliveryTime
        );
        
        assertTrue(response.deliveryTime().isAfter(response.confirmedAt()));
    }
}

