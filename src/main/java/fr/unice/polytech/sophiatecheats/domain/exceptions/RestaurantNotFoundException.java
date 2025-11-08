package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée quand un restaurant demandé n'est pas trouvé.
 * Utilisée dans l'US #104 pour les scénarios d'erreur de gestion des restaurants.
 */
public class RestaurantNotFoundException extends BusinessException {

    public RestaurantNotFoundException(String restaurantId) {
        super("Restaurant with ID '" + restaurantId + "' not found", "RESTAURANT_NOT_FOUND");
    }
}
