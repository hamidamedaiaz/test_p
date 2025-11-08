package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'un plat n'est pas trouvé dans le système.
 */
public class DishNotFoundException extends RuntimeException {
    public DishNotFoundException(String message) {
        super(message);
    }

    public DishNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
