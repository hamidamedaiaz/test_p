package fr.unice.polytech.sophiatecheats.application.dto.user.request;

import java.util.UUID;

/**
 * Requête pour supprimer un plat du panier.
 *
 * @param userId l'identifiant de l'utilisateur
 * @param dishId l'identifiant du plat à supprimer
 */
public record RemoveFromCartRequest(
    UUID userId,
    UUID dishId
) {
    public RemoveFromCartRequest {
        if (userId == null) {
            throw new IllegalArgumentException("L'identifiant utilisateur ne peut pas être null");
        }
        if (dishId == null) {
            throw new IllegalArgumentException("L'identifiant du plat ne peut pas être null");
        }
    }
}
