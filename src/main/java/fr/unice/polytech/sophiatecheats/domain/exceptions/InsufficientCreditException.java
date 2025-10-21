package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'un utilisateur tente d'effectuer une opération
 * nécessitant plus de crédit qu'il n'en possède.
 */
public class InsufficientCreditException extends DomainException {

    public InsufficientCreditException(String message) {
        super(message);
    }

    public InsufficientCreditException(String message, Throwable cause) {
        super(message, cause);
    }
}
