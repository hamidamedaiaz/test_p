package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stratégie de paiement par carte bancaire externe.
 *
 * Cette stratégie concrète implémente le paiement via une carte bancaire externe.
 * Elle simule l'intégration avec un service de paiement tiers (Stripe, PayPal, etc.).
 *
 * Fonctionnalités :
 * - Simule l'appel à un service de paiement externe
 * - Gère les cas d'erreur (service indisponible, paiement refusé)
 * - Peut être facilement remplacée par une vraie implémentation
 *
 * Note : Cette implémentation est une simulation. En production, elle devrait
 * communiquer avec un vrai service de paiement (Stripe, PayPal, etc.).
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
public class ExternalCardStrategy implements PaymentStrategy {

    private static final BigDecimal MAX_TRANSACTION_AMOUNT = new BigDecimal("500.00");
    private static final BigDecimal MIN_TRANSACTION_AMOUNT = new BigDecimal("0.01");

    // Simule la disponibilité du service de paiement externe
    private boolean serviceAvailable = true;

    @Override
    public PaymentResult processPayment(BigDecimal amount, User user) {
        // Validation des paramètres
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Montant invalide", "INVALID_AMOUNT");
        }

        if (user == null) {
            return PaymentResult.failure("Utilisateur invalide", "INVALID_USER");
        }

        // Vérifier les limites de transaction
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

        // Vérifier la disponibilité du service
        if (!isAvailable()) {
            return PaymentResult.failure(
                "Service de paiement temporairement indisponible",
                "SERVICE_UNAVAILABLE"
            );
        }

        // Vérifier si l'utilisateur peut payer
        if (!canPay(user, amount)) {
            return PaymentResult.failure(
                "Impossible de traiter le paiement avec cette carte",
                "PAYMENT_DECLINED"
            );
        }

        // Simuler l'appel au service de paiement externe
        try {
            // En production, ici on appellerait l'API du service de paiement
            // Exemple : stripeService.charge(amount, user.getCardToken())

            // Simulation : 5% de chance d'échec pour tester la robustesse
            if (Math.random() < 0.05) {
                return PaymentResult.failure(
                    "Paiement refusé par la banque",
                    "CARD_DECLINED"
                );
            }

            // Générer un ID de transaction
            String transactionId = "EXT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            // Simuler un délai de traitement (en production, ceci serait asynchrone)
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

        // Pour les paiements par carte bancaire externe, on ne vérifie PAS le crédit étudiant
        // La validation du paiement se fait côté banque/service de paiement externe
        // On accepte tous les montants valides (positifs et dans les limites de transaction)
        return amount.compareTo(MIN_TRANSACTION_AMOUNT) >= 0
            && amount.compareTo(MAX_TRANSACTION_AMOUNT) <= 0;
    }

    @Override
    public String getStrategyName() {
        return "External Card Payment";
    }

    @Override
    public boolean isAvailable() {
        return serviceAvailable;
    }

    /**
     * Permet de simuler l'indisponibilité du service (pour les tests).
     */
    public void setServiceAvailable(boolean available) {
        this.serviceAvailable = available;
    }

    /**
     * Simule un délai de traitement du paiement.
     */
    private void simulateProcessingDelay() {
        // En production, ceci serait géré de manière asynchrone
        try {
            Thread.sleep(100); // 100ms de délai simulé
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
