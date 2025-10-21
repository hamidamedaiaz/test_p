package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.math.BigDecimal;

/**
 * Contexte pour le traitement des paiements utilisant le Strategy Pattern.
 *
 * Cette classe agit comme un contexte qui délègue le traitement du paiement
 * à la stratégie appropriée. Elle simplifie l'utilisation des stratégies
 * pour les clients.
 *
 * Responsabilités :
 * - Maintenir une référence à une stratégie de paiement
 * - Déléguer les opérations de paiement à la stratégie active
 * - Permettre de changer de stratégie dynamiquement
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
public class PaymentContext {

    private PaymentStrategy strategy;

    /**
     * Constructeur avec une stratégie initiale.
     */
    public PaymentContext(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La stratégie de paiement ne peut pas être null");
        }
        this.strategy = strategy;
    }

    /**
     * Constructeur qui crée automatiquement la stratégie selon la méthode de paiement.
     */
    public PaymentContext(PaymentMethod paymentMethod) {
        this.strategy = PaymentStrategyFactory.createStrategy(paymentMethod);
    }

    /**
     * Change la stratégie de paiement à utiliser.
     */
    public void setStrategy(PaymentStrategy strategy) {
        if (strategy == null) {
            throw new IllegalArgumentException("La stratégie de paiement ne peut pas être null");
        }
        this.strategy = strategy;
    }

    /**
     * Change la stratégie via une méthode de paiement.
     */
    public void setStrategy(PaymentMethod paymentMethod) {
        this.strategy = PaymentStrategyFactory.createStrategy(paymentMethod);
    }

    /**
     * Traite un paiement en utilisant la stratégie courante.
     */
    public PaymentResult executePayment(BigDecimal amount, User user) {
        return strategy.processPayment(amount, user);
    }

    /**
     * Vérifie si l'utilisateur peut payer avec la stratégie courante.
     */
    public boolean canUserPay(User user, BigDecimal amount) {
        return strategy.canPay(user, amount);
    }

    /**
     * Retourne le nom de la stratégie courante.
     */
    public String getCurrentStrategyName() {
        return strategy.getStrategyName();
    }

    /**
     * Vérifie si la stratégie courante est disponible.
     */
    public boolean isStrategyAvailable() {
        return strategy.isAvailable();
    }
}

