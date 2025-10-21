package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception de base pour toutes les erreurs métier.
 * Utilisée dans le cadre de l'US #104 pour une gestion cohérente des erreurs.
 */
public abstract class BusinessException extends RuntimeException {
    private final String errorCode;

    protected BusinessException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    protected BusinessException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public String getErrorCode() {
        return errorCode;
    }
}
