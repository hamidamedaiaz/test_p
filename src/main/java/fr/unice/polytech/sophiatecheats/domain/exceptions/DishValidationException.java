package fr.unice.polytech.sophiatecheats.domain.exceptions;

public class DishValidationException extends RuntimeException {
    public DishValidationException(String message) {
        super(message);
    }
}
