package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception lancée lorsqu'une entité demandée n'est pas trouvée.
 */
public class EntityNotFoundException extends DomainException {

    public EntityNotFoundException(String message) {
        super(message);
    }

    public EntityNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}