package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request;

import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Requête pour ajouter un plat à un restaurant.
 */
public record AddDishToRestaurantRequest(
    UUID restaurantId,
    String dishName,
    String dishDescription,
    BigDecimal dishPrice,
    DishCategory dishCategory,
    boolean isAvailable
) {
    public AddDishToRestaurantRequest {
        if (restaurantId == null) {
            throw new IllegalArgumentException("L'ID du restaurant ne peut pas être null");
        }
        if (dishName == null || dishName.trim().isEmpty()) {
            throw new IllegalArgumentException("Le nom du plat ne peut pas être vide");
        }
        if (dishPrice == null || dishPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Le prix du plat doit être positif ou nul");
        }
        if (dishCategory == null) {
            throw new IllegalArgumentException("La catégorie du plat doit être définie");
        }
    }
}
