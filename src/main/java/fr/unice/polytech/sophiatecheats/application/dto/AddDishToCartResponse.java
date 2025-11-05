package fr.unice.polytech.sophiatecheats.application.dto;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Réponse après ajout d'un plat au panier.
 *
 * <p>Cette réponse contient l'état mis à jour du panier après l'ajout
 * d'un plat, incluant le nouveau total et le nombre d'articles.</p>
 *
 * @param cartId l'identifiant du panier
 * @param totalItems le nombre total d'articles dans le panier
 * @param totalAmount le montant total du panier
 * @param success indique si l'opération a réussi
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public record AddDishToCartResponse(
    UUID cartId,
    int totalItems,
    BigDecimal totalAmount,
    boolean success
) {
    /**
     * Constructeur compact avec validation.
     */
    public AddDishToCartResponse {
        if (success && cartId == null) {
            throw new IllegalArgumentException("CartId ne peut pas être null si l'opération a réussi");
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }

    }
}
