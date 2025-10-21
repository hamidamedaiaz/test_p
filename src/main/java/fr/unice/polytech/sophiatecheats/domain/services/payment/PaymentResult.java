package fr.unice.polytech.sophiatecheats.domain.services.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;


public record PaymentResult(
        boolean success,
        String transactionId,
        String message,
        BigDecimal processedAmount,
        LocalDateTime timestamp,
        String errorCode
) {

    /**
     * Crée un résultat de paiement réussi.
     */
    public static PaymentResult success(String transactionId, BigDecimal amount, String message) {
        return new PaymentResult(
                true,
                transactionId,
                message,
                amount,
                LocalDateTime.now(),
                null
        );
    }

    /**
     * Crée un résultat de paiement échoué.
     */
    public static PaymentResult failure(String message, String errorCode) {
        return new PaymentResult(
                false,
                null,
                message,
                BigDecimal.ZERO,
                LocalDateTime.now(),
                errorCode
        );
    }

    /**
     * Crée un résultat de paiement échoué sans code d'erreur.
     */
    public static PaymentResult failure(String message) {
        return failure(message, "PAYMENT_FAILED");
    }
}

