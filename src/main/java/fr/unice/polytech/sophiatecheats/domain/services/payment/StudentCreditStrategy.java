package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Stratégie de paiement par crédit étudiant.
 *
 * Cette stratégie concrète implémente le paiement via le crédit étudiant
 * disponible dans le compte de l'utilisateur.
 *
 * Fonctionnalités :
 * - Vérifie que l'utilisateur a suffisamment de crédit
 * - Déduit le montant du crédit étudiant
 * - Gère les cas d'erreur (solde insuffisant, compte invalide)
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
public class StudentCreditStrategy implements PaymentStrategy {

    private static final BigDecimal MINIMUM_BALANCE = BigDecimal.ZERO;

    @Override
    public PaymentResult processPayment(BigDecimal amount, User user) {
        // Validation des paramètres
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return PaymentResult.failure("Montant invalide", "INVALID_AMOUNT");
        }

        if (user == null) {
            return PaymentResult.failure("Utilisateur invalide", "INVALID_USER");
        }

        // Vérifier si l'utilisateur peut payer
        if (!canPay(user, amount)) {
            return PaymentResult.failure(
                String.format("Crédit étudiant insuffisant. Solde: %.2f€, Requis: %.2f€",
                    user.getStudentCredit(), amount.doubleValue()),
                "INSUFFICIENT_FUNDS"
            );
        }

        // Traiter le paiement
        try {
            BigDecimal currentCredit = user.getStudentCredit();
            BigDecimal newBalance = currentCredit.subtract(amount);
            user.setStudentCredit(newBalance);

            String transactionId = "STU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

            return PaymentResult.success(
                transactionId,
                amount,
                String.format("Paiement de %.2f€ effectué avec succès par crédit étudiant. Nouveau solde: %.2f€",
                    amount.doubleValue(), newBalance.doubleValue())
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

        // Vérifier que l'utilisateur a assez de crédit
        BigDecimal studentCredit = user.getStudentCredit();

        return studentCredit.compareTo(amount) >= 0 &&
               studentCredit.compareTo(MINIMUM_BALANCE) >= 0;
    }

    @Override
    public String getStrategyName() {
        return "Student Credit Payment";
    }

    @Override
    public boolean isAvailable() {
        // Le crédit étudiant est toujours disponible
        return true;
    }
}
