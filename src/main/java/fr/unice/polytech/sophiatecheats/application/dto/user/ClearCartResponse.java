package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.math.BigDecimal;
import java.util.UUID;


public record ClearCartResponse(UUID cartId, int itemCount, BigDecimal totalAmount, boolean success,
                                String message) implements DTO {
    /**
     * Constructeur complet.
     *
     * @param cartId      L'identifiant du panier (peut être null en cas d'erreur)
     * @param itemCount   Le nombre d'items restants (devrait être 0 après vidage)
     * @param totalAmount Le montant total (devrait être 0 après vidage)
     * @param success     Indique si l'opération a réussi
     * @param message     Message de succès ou d'erreur
     */
    public ClearCartResponse {
    }

    @Override
    public String toString() {
        return "ClearCartResponse{" +
                "cartId=" + cartId +
                ", itemCount=" + itemCount +
                ", totalAmount=" + totalAmount +
                ", success=" + success +
                ", message='" + message + '\'' +
                '}';
    }
}
