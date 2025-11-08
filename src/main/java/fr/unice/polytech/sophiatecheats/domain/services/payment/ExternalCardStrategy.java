package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.UUID;

public class ExternalCardStrategy implements PaymentStrategy {

    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("500.00");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("0.01");
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private boolean serviceAvailable = true;
    private boolean alwaysSucceed = false; // Mode test pour désactiver l'échec aléatoire

    @Override
    public PaymentResult processPayment(BigDecimal amount, User user) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Montant invalide", "INVALID_AMOUNT");
        }

        if (user == null) {
            return PaymentResult.failure("Utilisateur invalide", "INVALID_USER");
        }

        if (amount.compareTo(MIN_TRANSACTION_AMOUNT) < 0) {
            return PaymentResult.failure(
                String.format("Montant minimum requis: %.2f€", MIN_TRANSACTION_AMOUNT),
                "AMOUNT_TOO_LOW"
            );
        }

        if (amount.compareTo(MAX_TRANSACTION_AMOUNT) > 0) {
            return PaymentResult.failure(
                String.format("Montant maximum autorisé: %.2f€", MAX_TRANSACTION_AMOUNT),
                "AMOUNT_TOO_HIGH"
            );
        }

        if (!isAvailable()) {
            return PaymentResult.failure(
                "Service de paiement temporairement indisponible",
                "SERVICE_UNAVAILABLE"
            );
        }

        if (!canPay(user, amount)) {
            return PaymentResult.failure(
                "Impossible de traiter le paiement avec cette carte",
                "PAYMENT_DECLINED"
            );
        }

        try {
            // Simulation du 5% de refus bancaire (sauf en mode test)
            if (!alwaysSucceed && SECURE_RANDOM.nextDouble() < 0.05) {
                return PaymentResult.failure(
                    "Paiement refusé par la banque",
                    "CARD_DECLINED"
                );
            }

            String transactionId = "EXT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            simulateProcessingDelay();

            return PaymentResult.success(
                transactionId,
                amount,
                String.format("Paiement de %.2f€ effectué avec succès par carte bancaire",
                    amount.doubleValue())
            );

        } catch (Exception e) {
            return PaymentResult.failure(
                "Erreur lors du traitement du paiement: " + e.getMessage(),
                "PROCESSING_ERROR"
            );
        }
    }

    @Override
    public boolean canPay(User user, BigDecimal amount) {
        if (user == null || amount == null) {
            return false;
        }

        return amount.compareTo(MIN_TRANSACTION_AMOUNT) >= 0
            && amount.compareTo(MAX_TRANSACTION_AMOUNT) <= 0
            && serviceAvailable;
    }

    @Override
    public String getStrategyName() {
        return "External Card Payment";
    }

    @Override
    public boolean isAvailable() {
        return serviceAvailable;
    }

    public void setServiceAvailable(boolean available) {
        this.serviceAvailable = available;
    }

    /**
     * Active le mode test où les paiements réussissent toujours (pas d'échec aléatoire).
     * Utile pour les tests unitaires déterministes.
     */
    public void setAlwaysSucceed(boolean alwaysSucceed) {
        this.alwaysSucceed = alwaysSucceed;
    }

    private void simulateProcessingDelay() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}

