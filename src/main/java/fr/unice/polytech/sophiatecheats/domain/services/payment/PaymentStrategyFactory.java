package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

/**
 * Factory pour créer les stratégies de paiement appropriées.
 *
 * Ce pattern Factory Method simplifie la création des stratégies de paiement
 * et centralise la logique de sélection de la bonne stratégie.
 *
 * Avantages :
 * - Découplage : les clients n'ont pas besoin de connaître les classes concrètes
 * - Extensibilité : facile d'ajouter de nouvelles stratégies
 * - Maintenabilité : logique de création centralisée
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
public class PaymentStrategyFactory {

    /**
     * Crée une stratégie de paiement appropriée selon la méthode de paiement.
     *
     * @param paymentMethod Méthode de paiement souhaitée
     * @return Stratégie de paiement correspondante
     * @throws IllegalArgumentException si la méthode de paiement est null ou non supportée
     */
    public static PaymentStrategy createStrategy(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            throw new IllegalArgumentException("La méthode de paiement ne peut pas être null");
        }

        return switch (paymentMethod) {
            case STUDENT_CREDIT -> new StudentCreditStrategy();
            case EXTERNAL_CARD -> new ExternalCardStrategy();
            default -> throw new IllegalArgumentException(
                "Méthode de paiement non supportée: " + paymentMethod
            );
        };
    }

    /**
     * Vérifie si une méthode de paiement est supportée.
     *
     * @param paymentMethod Méthode de paiement à vérifier
     * @return true si la méthode est supportée, false sinon
     */
    public static boolean isSupported(PaymentMethod paymentMethod) {
        if (paymentMethod == null) {
            return false;
        }

        try {
            createStrategy(paymentMethod);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Retourne la liste des méthodes de paiement supportées.
     *
     * @return Tableau des méthodes de paiement supportées
     */
    public static PaymentMethod[] getSupportedMethods() {
        return new PaymentMethod[] {
            PaymentMethod.STUDENT_CREDIT,
            PaymentMethod.EXTERNAL_CARD
        };
    }
}
