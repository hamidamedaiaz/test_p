package fr.unice.polytech.sophiatecheats.application.dto;

import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO pour la requête de passage de commande
 */
@Getter
@AllArgsConstructor
public class PlaceOrderRequest {
    private final UUID userId;
    private final UUID restaurantId;
    private final List<OrderItemRequest> items;
    private final LocalDateTime requestedDeliveryTime;
    private final PaymentMethod paymentMethod;

    @Getter
    @AllArgsConstructor
    public static class OrderItemRequest {
        private final UUID dishId;
        private final int quantity;
    }
}
