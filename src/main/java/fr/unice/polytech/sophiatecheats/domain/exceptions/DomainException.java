package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception de base pour toutes les exceptions métier du domaine SophiaTech Eats.
 */
public abstract class DomainException extends RuntimeException {

  protected DomainException(String message) {
    super(message);
  }

  protected DomainException(String message, Throwable cause) {
    super(message, cause);
  }
}
