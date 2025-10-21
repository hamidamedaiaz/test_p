package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'on tente de confirmer une commande déjà confirmée.
 */
public class OrderAlreadyConfirmedException extends BusinessException {

    public OrderAlreadyConfirmedException(String orderId) {
        super("La commande " + orderId + " est déjà confirmée", "ORDER_ALREADY_CONFIRMED");
    }
}
