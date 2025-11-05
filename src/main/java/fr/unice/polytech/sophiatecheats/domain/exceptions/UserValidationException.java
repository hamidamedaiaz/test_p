package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'une validation d'utilisateur échoue.
 */
public class UserValidationException extends DomainException {

    public UserValidationException(String message) {
        super(message);
    }

    public UserValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
