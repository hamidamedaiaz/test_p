package fr.unice.polytech.sophiatecheats.application.dto.user.response;

import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO for placing an order.
 */
public record PlaceOrderResponse(
    String orderId,
    String customerName,
    String restaurantName,
    BigDecimal totalAmount,
    OrderStatus status,
    PaymentMethod paymentMethod,
    LocalDateTime orderDateTime
) {}