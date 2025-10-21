package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import java.util.UUID;

/**
 * Request DTO for placing an order from the user's active cart.
 * @param userId l'identifiant de l'utilisateur qui passe commande
 * @param restaurantId l'identifiant du restaurant (pour validation)
 * @param paymentMethod le moyen de paiement choisi (STUDENT_CREDIT ou EXTERNAL_CARD)
 */
public record PlaceOrderRequest(
    UUID userId,
    UUID restaurantId,
    PaymentMethod paymentMethod
) {
    /**
     * Valide que tous les champs obligatoires sont présents.
     *
     * @return true si la requête est valide
     */
    public boolean isValid() {
        return userId != null && 
               restaurantId != null && 
               paymentMethod != null;
    }
}