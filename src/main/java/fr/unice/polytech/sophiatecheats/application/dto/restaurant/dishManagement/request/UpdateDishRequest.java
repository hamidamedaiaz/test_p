package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request;

import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Requête pour modifier un plat existant.
 * Tous les champs sont optionnels sauf restaurantId et dishId.
 */
public record UpdateDishRequest(
    UUID restaurantId,
    UUID dishId,
    String newName,
    String newDescription,
    BigDecimal newPrice,
    DishCategory newCategory,
    Boolean newAvailability
) {
    public UpdateDishRequest {
        if (restaurantId == null) {
            throw new IllegalArgumentException("L'ID du restaurant ne peut pas être null");
        }
        if (dishId == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }
        // Validation conditionnelle des nouveaux champs
        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nouveau nom du plat ne peut pas être vide");
        }
        if (newPrice != null && newPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le nouveau prix du plat doit être positif ou nul");
        }
    }
}
