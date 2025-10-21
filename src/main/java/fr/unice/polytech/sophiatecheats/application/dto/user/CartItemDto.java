package fr.unice.polytech.sophiatecheats.application.dto.user;

import java.math.BigDecimal;
import java.util.UUID; /**
 * DTO représentant un article dans le panier pour les réponses.
 *
 * @param dishId l'identifiant du plat
 * @param dishName le nom du plat
 * @param dishDescription la description du plat
 * @param unitPrice le prix unitaire
 * @param quantity la quantité
 * @param subtotal le sous-total (prix unitaire × quantité)
 */
public record CartItemDto(
    UUID dishId,
    String dishName,
    String dishDescription,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal
) {}
