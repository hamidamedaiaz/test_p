package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'on tente d'effectuer une opération sur une commande expirée.
 */
public class OrderExpiredException extends BusinessException {

    public OrderExpiredException(String orderId) {
        super("La commande " + orderId + " a expiré et ne peut plus être modifiée", "ORDER_EXPIRED");
    }
}
