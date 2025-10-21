package fr.unice.polytech.sophiatecheats.domain.exceptions;

public class RestaurantValidationException extends RuntimeException {
    public RestaurantValidationException(String message) {
        super(message);
    }
}
