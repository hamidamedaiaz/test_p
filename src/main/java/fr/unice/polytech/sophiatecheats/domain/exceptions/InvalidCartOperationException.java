package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lors d'opérations invalides sur le panier.
 * Utilisée dans l'US #104 pour les scénarios d'erreur de gestion du panier.
 */
public class InvalidCartOperationException extends BusinessException {

    public InvalidCartOperationException(String message) {
        super(message, "INVALID_CART_OPERATION");
    }

    public static InvalidCartOperationException quantityMustBePositive() {
        return new InvalidCartOperationException("Quantity must be greater than 0");
    }

    public static InvalidCartOperationException itemNotFound(String itemName) {
        return new InvalidCartOperationException("Item '" + itemName + "' not found in cart");
    }

    public static InvalidCartOperationException cannotMixRestaurants() {
        return new InvalidCartOperationException("Cannot mix items from different restaurants");
    }

    public static InvalidCartOperationException maxCartValueExceeded() {
        return new InvalidCartOperationException("Cart total exceeds maximum allowed amount");
    }
}
