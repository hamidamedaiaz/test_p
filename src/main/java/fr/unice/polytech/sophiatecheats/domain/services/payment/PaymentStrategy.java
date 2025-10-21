package fr.unice.polytech.sophiatecheats.domain.services.payment;

import fr.unice.polytech.sophiatecheats.domain.entities.user.User;

import java.math.BigDecimal;

/**
 * Interface Strategy pour le traitement des paiements.
 *
 * Ce pattern permet de définir une famille d'algorithmes de paiement,
 * de les encapsuler et de les rendre interchangeables.
 *
 * Avantages du Strategy Pattern :
 * - Séparation des préoccupations : chaque stratégie gère sa propre logique
 * - Extensibilité : facile d'ajouter de nouvelles méthodes de paiement
 * - Testabilité : chaque stratégie peut être testée indépendamment
 * - Principe Open/Closed : ouvert à l'extension, fermé à la modification
 *
 * @author SophiaTech Eats Team
 * @version 1.0
 */
public interface PaymentStrategy {

    /**
     * Traite un paiement pour un utilisateur donné.
     *
     * @param amount Montant à payer
     * @param user Utilisateur effectuant le paiement
     * @return PaymentResult contenant le statut et les détails du paiement
     */
    PaymentResult processPayment(BigDecimal amount, User user);

    /**
     * Vérifie si l'utilisateur peut effectuer un paiement avec cette stratégie.
     *
     * @param user Utilisateur à vérifier
     * @param amount Montant du paiement
     * @return true si l'utilisateur peut payer, false sinon
     */
    boolean canPay(User user, BigDecimal amount);

    /**
     * Retourne le nom de la stratégie de paiement.
     *
     * @return Nom de la stratégie (ex: "Student Credit", "External Card")
     */
    String getStrategyName();

    /**
     * Vérifie la disponibilité de cette méthode de paiement.
     *
     * @return true si la méthode de paiement est disponible, false sinon
     */
    default boolean isAvailable() {
        return true;
    }
}

