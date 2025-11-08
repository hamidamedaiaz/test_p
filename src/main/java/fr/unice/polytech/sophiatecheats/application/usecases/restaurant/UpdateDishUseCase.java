package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.request.UpdateDishRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.response.UpdateDishResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;

import java.util.Optional;

/**
 * Use case pour modifier un plat existant dans un restaurant.
 * Permet la modification partielle ou complète des propriétés d'un plat.
 */
public class UpdateDishUseCase implements UseCase<UpdateDishRequest, UpdateDishResponse> {

    private final RestaurantRepository restaurantRepository;

    public UpdateDishUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public UpdateDishResponse execute(UpdateDishRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande de modification de plat ne peut pas être nulle");
        }

        try {
            Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                    "Restaurant non trouvé avec l'ID: " + request.restaurantId()));

            Optional<Dish> existingDish = restaurant.findDishById(request.dishId());
            if (existingDish.isEmpty()) {
                return new UpdateDishResponse(
                    request.dishId(),
                    "Plat non trouvé avec l'ID: " + request.dishId(),
                    false
                );
            }

            // Modification des propriétés du plat selon les paramètres fournis
            updateDishProperties(restaurant, request);

            // Sauvegarde du restaurant modifié
            restaurantRepository.save(restaurant);

            // Récupération du plat modifié pour la réponse
            Dish modifiedDish = restaurant.findDishById(request.dishId()).orElseThrow();

            return new UpdateDishResponse(
                request.dishId(),
                "Plat '" + modifiedDish.getName() + "' modifié avec succès",
                true
            );

        } catch (IllegalArgumentException | RestaurantNotFoundException e) {
            return new UpdateDishResponse(
                request.dishId(),
                e.getMessage(),
                false
            );
        } catch (Exception e) {
            return new UpdateDishResponse(
                request.dishId(),
                "Erreur lors de la modification du plat: " + e.getMessage(),
                false
            );
        }
    }

    /**
     * Met à jour les propriétés du plat selon la demande.
     * Centralise la logique de modification pour éviter la duplication.
     */
    private void updateDishProperties(Restaurant restaurant, UpdateDishRequest request) {
        // Optimisation: récupérer le plat une seule fois
        Optional<Dish> dishOpt = restaurant.findDishById(request.dishId());
        if (dishOpt.isEmpty()) {
            return; // Le plat n'existe pas, aucune modification possible
        }

        Dish dish = dishOpt.get();

        // Mise à jour des propriétés via les méthodes du Restaurant
        if (request.newName() != null) {
            restaurant.modifyDishName(request.dishId(), request.newName());
        }

        if (request.newDescription() != null) {
            restaurant.modifyDishDescription(request.dishId(), request.newDescription());
        }

        if (request.newPrice() != null) {
            restaurant.modifyDishPrice(request.dishId(), request.newPrice());
        }

        if (request.newCategory() != null) {
            restaurant.modifyDishCategory(request.dishId(), request.newCategory());
        }

        // Mise à jour de la disponibilité directement sur l'entité Dish
        if (request.newAvailability() != null) {
            // Récupération du plat mis à jour après les modifications précédentes
            Optional<Dish> updatedDish = restaurant.findDishById(request.dishId());
            if (updatedDish.isPresent()) {
                if (request.newAvailability()) {
                    updatedDish.get().makeAvailable();
                } else {
                    updatedDish.get().makeUnavailable();
                }
            }
        }
    }
}
