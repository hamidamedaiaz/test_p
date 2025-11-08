package fr.unice.polytech.sophiatecheats.application.dto.user.response;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.UUID;

/**
 * Réponse pour l'annulation d'un panier.
 *
 * @param cancelledCartId L'identifiant du panier supprimé (null en cas d'erreur)
 * @param success         Indique si l'opération a réussi
 * @param message         Message de succès ou d'erreur
 */
public record CancelCartResponse(UUID cancelledCartId, boolean success, String message) implements DTO {
}

