package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response;

import java.util.UUID;

/**
 * Réponse pour l'ajout d'un plat à un restaurant.
 */
public record AddDishToRestaurantResponse(
    UUID dishId,
    String message,
    boolean success
) {}
