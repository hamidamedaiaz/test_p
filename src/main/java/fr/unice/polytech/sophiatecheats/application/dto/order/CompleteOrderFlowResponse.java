package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.time.LocalDateTime;

/**
 * Response DTO pour le flux complet order→slot→payment→confirmation.
 */
public record CompleteOrderFlowResponse(
    String orderId,
    boolean success,
    String message,
    LocalDateTime slotStartTime,
    PaymentMethod paymentMethod,
    LocalDateTime deliveryTime
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty()
               && message != null;
    }
}
