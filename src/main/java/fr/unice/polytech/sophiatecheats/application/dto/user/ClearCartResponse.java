package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.math.BigDecimal;
import java.util.UUID;


public class ClearCartResponse implements DTO {
    private final UUID cartId;
    private final int itemCount;
    private final BigDecimal totalAmount;
    private final boolean success;
    private final String message;

    /**
     * Constructeur complet.
     *
     * @param cartId L'identifiant du panier (peut être null en cas d'erreur)
     * @param itemCount Le nombre d'items restants (devrait être 0 après vidage)
     * @param totalAmount Le montant total (devrait être 0 après vidage)
     * @param success Indique si l'opération a réussi
     * @param message Message de succès ou d'erreur
     */
    public ClearCartResponse(UUID cartId, int itemCount, BigDecimal totalAmount,
                           boolean success, String message) {
        this.cartId = cartId;
        this.itemCount = itemCount;
        this.totalAmount = totalAmount;
        this.success = success;
        this.message = message;
    }

    public UUID getCartId() {
        return cartId;
    }

    public int getItemCount() {
        return itemCount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
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
