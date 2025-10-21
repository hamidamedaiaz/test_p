package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.AddDishToRestaurantRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.AddDishToRestaurantResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DishValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.RestaurantNotFoundException;

public class AddDishToRestaurantUseCase implements UseCase<AddDishToRestaurantRequest, AddDishToRestaurantResponse> {
    private final RestaurantRepository restaurantRepository;

    public AddDishToRestaurantUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public AddDishToRestaurantResponse execute(AddDishToRestaurantRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("La demande d'ajout de plat ne peut pas être nulle");
        }
        try {
            Restaurant restaurant = restaurantRepository.findById(request.restaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException(
                    "Restaurant non trouvé avec l'ID: " + request.restaurantId()));

            Dish newDish = Dish.builder()
                .name(request.dishName())
                .description(request.dishDescription())
                .price(request.dishPrice())
                .category(request.dishCategory())
                .available(request.isAvailable())
                .build();

            restaurant.addDish(newDish);
            restaurantRepository.save(restaurant);
            return new AddDishToRestaurantResponse(
                newDish.getId(),
                "Plat '" + newDish.getName() + "' ajouté avec succès au restaurant '" + restaurant.getName() + "'",
                true
            );
        } catch (DishValidationException | IllegalArgumentException | RestaurantNotFoundException e) {
            return new AddDishToRestaurantResponse(null, e.getMessage(), false);
        } catch (Exception e) {
            return new AddDishToRestaurantResponse(null, "Erreur inattendue: " + e.getMessage(), false);
        }
    }
}
