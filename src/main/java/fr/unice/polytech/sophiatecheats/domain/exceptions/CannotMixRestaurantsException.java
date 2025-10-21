package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lorsqu'un utilisateur tente d'ajouter au panier
 * un plat provenant d'un restaurant différent de celui des plats déjà présents.
 *
 * Règle métier : Un panier ne peut contenir que des plats d'un seul restaurant.
 */
public class CannotMixRestaurantsException extends BusinessException {

  public CannotMixRestaurantsException(String currentRestaurantName, String attemptedRestaurantName) {
    super(
            String.format("Cannot mix items from different restaurants. " +
                            "Your cart contains items from '%s'. " +
                            "Please complete or clear your order before adding items from '%s'.",
                    currentRestaurantName, attemptedRestaurantName),
            "CANNOT_MIX_RESTAURANTS"
    );
  }

  public CannotMixRestaurantsException() {
    super(
            "Cannot mix items from different restaurants",
            "CANNOT_MIX_RESTAURANTS"
    );
  }
}