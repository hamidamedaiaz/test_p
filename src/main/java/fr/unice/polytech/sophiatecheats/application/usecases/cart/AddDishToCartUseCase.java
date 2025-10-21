package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
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
            User user = validateAndGetUser(request.userId());
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

        } catch (CannotMixRestaurantsException e) {
            // Gérer spécifiquement l'erreur de mélange de restaurants
            System.out.println("[AddDishToCartUseCase] Failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return new AddDishToCartResponse(
                    null,
                    0,
                    BigDecimal.ZERO,
                    false
            );
        } catch (ValidationException | EntityNotFoundException e) {
            System.out.println("[AddDishToCartUseCase] Failed: " + e.getClass().getSimpleName() + " - " + e.getMessage());
            return new AddDishToCartResponse(
                    null,
                    0,
                    BigDecimal.ZERO,
                    false
            );
        }
    }

    private User validateAndGetUser(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with ID: " + userId));
    }

    private Dish validateAndGetDish(UUID dishId) {
        System.out.println("[AddDishToCartUseCase] Searching dishId=" + dishId);
        // Appeler findAll() une seule fois et réutiliser la liste pour le debug et la recherche
        var restaurants = restaurantRepository.findAll();
        restaurants.forEach(r -> {
            System.out.println("[AddDishToCartUseCase] Restaurant=" + r.getName() + " dishes=" + r.getMenu().size());
            r.getMenu().forEach(d -> System.out.println("  - Dish name=" + d.getName() + " id=" + d.getId()));
        });

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