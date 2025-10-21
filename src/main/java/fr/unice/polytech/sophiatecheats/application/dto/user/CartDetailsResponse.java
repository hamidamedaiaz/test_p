package fr.unice.polytech.sophiatecheats.application.dto.user;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Réponse contenant les détails complets d'un panier.
 *
 * <p>Cette réponse est utilisée pour afficher le contenu du panier
 * avec tous les détails des articles et le total calculé.</p>
 *
 * @param cartId l'identifiant du panier
 * @param userId l'identifiant de l'utilisateur propriétaire
 * @param items la liste des articles dans le panier
 * @param totalAmount le montant total du panier
 * @param totalItems le nombre total d'articles
 * @param isEmpty indique si le panier est vide
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public record CartDetailsResponse(
    UUID cartId,
    UUID userId,
    List<CartItemDto> items,
    BigDecimal totalAmount,
    int totalItems,
    boolean isEmpty
) {
    /**
     * Constructeur compact avec validation.
     */
    public CartDetailsResponse {
        if (cartId == null) {
            throw new IllegalArgumentException("CartId ne peut pas être null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("UserId ne peut pas être null");
        }
        if (items == null) {
            throw new IllegalArgumentException("Items ne peut pas être null");
        }
        if (totalAmount == null) {
            totalAmount = BigDecimal.ZERO;
        }
    }
}

