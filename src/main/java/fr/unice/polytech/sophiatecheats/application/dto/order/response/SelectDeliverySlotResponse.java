package fr.unice.polytech.sophiatecheats.application.dto.order.response;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Response DTO pour la sélection d'un créneau de livraison.
 */
public record SelectDeliverySlotResponse(
    String orderId,
    UUID slotId,
    LocalDateTime slotStartTime,
    LocalDateTime slotEndTime,
    String message
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty()
               && slotId != null
               && slotStartTime != null
               && slotEndTime != null;
    }
}
