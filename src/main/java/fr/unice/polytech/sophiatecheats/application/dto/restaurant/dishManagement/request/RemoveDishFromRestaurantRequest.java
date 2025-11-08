package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request;

import java.util.UUID;

/**
 * Requête pour supprimer un plat d'un restaurant.
 */
public record RemoveDishFromRestaurantRequest(
    UUID restaurantId,
    UUID dishId
) {
    public RemoveDishFromRestaurantRequest {
        if (restaurantId == null) {
            throw new IllegalArgumentException("L'ID du restaurant ne peut pas être null");
        }
        if (dishId == null) {
            throw new IllegalArgumentException("L'ID du plat ne peut pas être null");
        }
    }
}
