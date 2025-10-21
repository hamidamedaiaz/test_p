package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.math.BigDecimal;

/**
 * Response DTO pour le traitement d'un paiement.
 */
public record ProcessPaymentResponse(
    String orderId,
    boolean success,
    String message,
    PaymentMethod paymentMethod,
    BigDecimal amount
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty()
               && message != null
               && paymentMethod != null
               && amount != null;
    }
}
