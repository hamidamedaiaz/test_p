package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CannotMixRestaurantsException;

import java.math.BigDecimal;
import java.util.UUID;

public class AddDishToCartUseCase implements UseCase<AddDishToCartRequest, AddDishToCartResponse> {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final CartRepository cartRepository;

    public AddDishToCartUseCase(UserRepository userRepository,
                                RestaurantRepository restaurantRepository,
                                CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
        this.cartRepository = cartRepository;
    }

    @Override
    public AddDishToCartResponse execute(AddDishToCartRequest request) {
        try {
            // Valider que l'utilisateur existe
            validateAndGetUser(request.userId());

            Dish dish = validateAndGetDish(request.dishId());

            // Trouver le restaurant du plat
            Restaurant restaurant = findRestaurantByDishId(request.dishId());

            Cart cart = getOrCreateActiveCart(request.userId());

            // Ajouter le plat avec l'ID du restaurant
            cart.addDish(dish, request.quantity(), restaurant.getId());

            cartRepository.save(cart);

            return new AddDishToCartResponse(
                    cart.getId(),
                    cart.getTotalItems(),
                    cart.calculateTotal(),
                    true
            );

        } catch (CannotMixRestaurantsException | ValidationException | EntityNotFoundException e) {
            // Gérer les erreurs de validation et d'entités non trouvées
            return new AddDishToCartResponse(
                    null,
                    0,
                    BigDecimal.ZERO,
                    false
            );
        }
    }

    private void validateAndGetUser(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private Dish validateAndGetDish(UUID dishId) {
        // Récupérer tous les restaurants et chercher le plat
        var restaurants = restaurantRepository.findAll();

        return restaurants.stream()
                .flatMap(restaurant -> restaurant.getMenu().stream())
                .filter(dish -> dish.getId().equals(dishId))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Dish not found with ID: " + dishId));
    }

    /**
     *  Trouve le restaurant qui possède un plat spécifique.
     */
    private Restaurant findRestaurantByDishId(UUID dishId) {
        return restaurantRepository.findAll().stream()
                .filter(restaurant -> restaurant.getMenu().stream()
                        .anyMatch(dish -> dish.getId().equals(dishId)))
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("Restaurant not found for dish with ID: " + dishId));
    }

    private Cart getOrCreateActiveCart(UUID userId) {
        return cartRepository.findActiveCartByUserId(userId)
                .orElseGet(() -> new Cart(userId));
    }
}