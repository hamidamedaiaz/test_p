package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.RemoveDishFromRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.RemoveDishFromRestaurantResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;

import java.util.Optional;

/**
 * Use case pour supprimer un plat d'un restaurant.
 * Vérifie l'existence du plat avant suppression pour éviter les erreurs silencieuses.
 */
public class RemoveDishFromRestaurantUseCase implements UseCase<RemoveDishFromRestaurantRequest, RemoveDishFromRestaurantResponse> {

    private final RestaurantRepository restaurantRepository;

    public RemoveDishFromRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public RemoveDishFromRestaurantResponse execute(RemoveDishFromRestaurantRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de suppression de plat ne peut pas être nulle");
        }

        try {
            Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                    "Restaurant non trouvé avec l'ID: " + request.restaurantId()));

            Optional<Dish> existingDish = restaurant.findDishById(request.dishId());
            if (existingDish.isEmpty()) {
                return new RemoveDishFromRestaurantResponse(
                    request.dishId(),
                    "Plat non trouvé avec l'ID: " + request.dishId(),
                    false
                );
            }

            String dishName = existingDish.get().getName();

            // Suppression du plat (logique métier dans le domaine)
            restaurant.removeDish(request.dishId());

            // Sauvegarde du restaurant modifié
            restaurantRepository.save(restaurant);

            return new RemoveDishFromRestaurantResponse(
                request.dishId(),
                "Plat '" + dishName + "' supprimé avec succès du restaurant '" + restaurant.getName() + "'",
                true
            );

        } catch (IllegalArgumentException | RestaurantNotFoundException e) {
            return new RemoveDishFromRestaurantResponse(
                request.dishId(),
                e.getMessage(),
                false
            );
        } catch (Exception e) {
            return new RemoveDishFromRestaurantResponse(
                request.dishId(),
                "Erreur lors de la suppression du plat: " + e.getMessage(),
                false
            );
        }
    }
}
