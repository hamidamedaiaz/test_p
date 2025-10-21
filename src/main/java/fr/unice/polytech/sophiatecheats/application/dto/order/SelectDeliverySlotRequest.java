package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.UUID;

/**
 * Request DTO pour sélectionner un créneau de livraison pour une commande.
 */
public record SelectDeliverySlotRequest(
    String orderId,
    UUID slotId
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty()
               && slotId != null;
    }
}
