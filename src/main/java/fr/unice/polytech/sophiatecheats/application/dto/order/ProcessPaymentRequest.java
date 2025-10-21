package fr.unice.polytech.sophiatecheats.application.dto.order;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

/**
 * Request DTO pour traiter le paiement d'une commande.
 */
public record ProcessPaymentRequest(
    String orderId,
    String cardToken  // For external card payments, null for student credit
) implements DTO {

    @Override
    public boolean isValid() {
        return orderId != null && !orderId.trim().isEmpty();
    }
}
