package fr.unice.polytech.sophiatecheats.domain.services;

import java.math.BigDecimal;

/**
 * Service de paiement gérant les paiements externes et les crédits étudiants.
 *
 * Pour les spécifications du projet:
 * - Un service de paiement externe mocké
 * - Gestion du crédit étudiant en interne
 * - Pas de paiements mixtes
 */
public class PaymentService {
    
    /**
     * Traite un paiement par carte externe (mocké).
     *
     * @param amount Montant à payer
     * @param cardToken Token de la carte (simulé)
     * @param orderId ID de la commande pour traçabilité
     * @return true si le paiement réussit, false sinon
     */
    public boolean processExternalPayment(BigDecimal amount, String cardToken, String orderId) {
        // Mock de service de paiement externe
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        if (cardToken == null || cardToken.trim().isEmpty()) {
            return false;
        }

        // Simulation: échec si le token contient "FAIL"
        if (cardToken.contains("FAIL")) {
            return false;
        }

        // Simulation: délai de traitement
        try {
            Thread.sleep(100); // Simule latence réseau
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }

        // Succès par défaut pour la simulation
        return true;
    }

    /**
     * Vérifie si un montant peut être payé avec une méthode donnée.
     * (Utilisé pour validation avant traitement)
     */
    public boolean canProcessPayment(BigDecimal amount, String paymentMethod) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }

        return paymentMethod != null && !paymentMethod.trim().isEmpty();
    }
}
