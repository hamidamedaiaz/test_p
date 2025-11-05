package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import java.util.UUID; /**
 * Requête pour mettre à jour la quantité d'un article dans le panier.
 *
 * @param userId l'identifiant de l'utilisateur
 * @param dishId l'identifiant du plat
 * @param newQuantity la nouvelle quantité (0 ou moins supprime l'article)
 */
public record UpdateCartItemRequest(
    UUID userId,
    UUID dishId,
    int newQuantity
) {
    public UpdateCartItemRequest {
        if (userId == null) {
            throw new IllegalArgumentException("L'identifiant utilisateur ne peut pas être null");
        }
        if (dishId == null) {
            throw new IllegalArgumentException("L'identifiant du plat ne peut pas être null");
        }
    }
}
