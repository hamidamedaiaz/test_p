package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * voila  c'est un Exception levée lorsqu'un utilisateur est bloqué pour cause de dette.
 * Cette exception est utilisée dans le contexte des règles métier de paiement.
 */
public class DebtBlockedException extends DomainException {


    public DebtBlockedException(String message) {
        super(message);
    }

    public DebtBlockedException(String message, Throwable cause) {
        super(message, cause);
    }
}
