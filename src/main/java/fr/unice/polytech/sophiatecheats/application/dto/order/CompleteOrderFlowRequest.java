package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.UUID;

/**
 * Request DTO pour le flux complet order→slot→payment→confirmation.
 */
public record CompleteOrderFlowRequest(
    String orderId,
    UUID slotId,
    String cardToken  // Null for student credit payments
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty()
               && slotId != null;
    }
}
