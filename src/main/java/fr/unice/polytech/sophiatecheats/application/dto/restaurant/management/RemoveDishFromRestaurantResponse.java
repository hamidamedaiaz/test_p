package fr.unice.polytech.sophiatecheats.application.dto.restaurant.management;

import java.util.UUID;

/**
 * Réponse pour la suppression d'un plat d'un restaurant.
 */
public record RemoveDishFromRestaurantResponse(
    UUID dishId,
    String message,
    boolean success
) {}
