package fr.unice.polytech.sophiatecheats.application.dto.order.response;

import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response DTO pour la confirmation d'une commande.
 */
public record ConfirmOrderResponse(
    String orderId,
    String customerName,
    String restaurantName,
    BigDecimal totalAmount,
    OrderStatus status,
    LocalDateTime confirmedAt,
    LocalDateTime deliveryTime
) {}
