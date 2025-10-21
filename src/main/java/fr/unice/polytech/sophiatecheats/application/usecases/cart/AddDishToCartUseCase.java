package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

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

            Cart cart = getOrCreateActiveCart(request.userId());

            cart.addDish(dish, request.quantity());

            cartRepository.save(cart);

            return new AddDishToCartResponse(
                cart.getId(),
                cart.getTotalItems(),
                cart.calculateTotal(),
                true
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
        restaurantRepository.findAll().forEach(r -> {
            System.out.println("[AddDishToCartUseCase] Restaurant=" + r.getName() + " dishes=" + r.getMenu().size());
            r.getMenu().forEach(d -> System.out.println("  - Dish name=" + d.getName() + " id=" + d.getId()));
        });
        return restaurantRepository.findAll().stream()
            .flatMap(restaurant -> restaurant.getMenu().stream())
            .filter(dish -> dish.getId().equals(dishId))
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Dish not found with ID: " + dishId));
    }

    private Cart getOrCreateActiveCart(UUID userId) {
        return cartRepository.findActiveCartByUserId(userId)
            .orElseGet(() -> {
                // Don't save here - we'll save after adding the dish
                return new Cart(userId);
            });
    }
}
