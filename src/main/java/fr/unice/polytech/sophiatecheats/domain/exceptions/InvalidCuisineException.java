package fr.unice.polytech.sophiatecheats.domain.exceptions;

/**
 * Exception levée lors de problèmes avec les types de cuisine ou filtres invalides.
 * Utilisée dans l'US #104 pour les scénarios d'erreur de filtrage des restaurants.
 */
public class InvalidCuisineException extends BusinessException {

    public InvalidCuisineException(String cuisineType) {
        super("Invalid cuisine type: " + cuisineType, "INVALID_CUISINE_TYPE");
    }
}
