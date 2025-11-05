package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.util.List;
import java.util.UUID;

/**
 * Request DTO for placing an order.
 */
public record PlaceOrderRequest(
    UUID userId,
    UUID restaurantId,
    List<OrderItemRequest> items,
    PaymentMethod paymentMethod
) {

    public record OrderItemRequest(
        UUID dishId,
        int quantity
    ) {}

    public boolean isValid() {
        return userId != null && 
               restaurantId != null && 
               items != null && !items.isEmpty() &&
               paymentMethod != null;
    }
}