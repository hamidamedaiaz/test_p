package fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response;

import java.util.UUID;

/**
 * Réponse pour la modification d'un plat.
 */
public record UpdateDishResponse(
    UUID dishId,
    String message,
    boolean success
) {}
