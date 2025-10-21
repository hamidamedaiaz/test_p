package fr.unice.polytech.sophiatecheats.application.dto.user;

import java.util.UUID;


public record AddDishToCartRequest(
    UUID userId,
    UUID dishId,
    int quantity
) {

    public AddDishToCartRequest {
        if (userId == null) {
            throw new IllegalArgumentException("L'identifiant utilisateur ne peut pas être null");
        }
        if (dishId == null) {
            throw new IllegalArgumentException("L'identifiant du plat ne peut pas être null");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("La quantité doit être positive, reçu: " + quantity);
        }
    }
}
