package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.UUID;

public class CancelCartResponse implements DTO {
    private final UUID cancelledCartId;
    private final boolean success;
    private final String message;

    /**
     * Constructeur complet.
     *
     * @param cancelledCartId L'identifiant du panier supprimé (null en cas d'erreur)
     * @param success Indique si l'opération a réussi
     * @param message Message de succès ou d'erreur
     */
    public CancelCartResponse(UUID cancelledCartId, boolean success, String message) {
        this.cancelledCartId = cancelledCartId;
        this.success = success;
        this.message = message;
    }

    public UUID getCancelledCartId() {
        return cancelledCartId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
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

