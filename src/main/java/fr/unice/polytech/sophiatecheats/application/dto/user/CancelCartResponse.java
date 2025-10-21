package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.UUID;

public record CancelCartResponse(UUID cancelledCartId, boolean success, String message) implements DTO {
    /**
     * Constructeur complet.
     *
     * @param cancelledCartId L'identifiant du panier supprimé (null en cas d'erreur)
     * @param success         Indique si l'opération a réussi
     * @param message         Message de succès ou d'erreur
     */
    public CancelCartResponse {
    }

    @Override
    public String toString() {
        return "CancelCartResponse{" +
                "cancelledCartId=" + cancelledCartId +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}

