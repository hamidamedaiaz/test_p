package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response;

import java.util.UUID;

/**
 * Réponse pour la suppression d'un plat d'un restaurant.
 */
public record RemoveDishFromRestaurantResponse(
    UUID dishId,
    String message,
    boolean success
) {}
