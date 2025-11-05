package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.Repository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

import java.util.UUID;


public class AddDishToCartUseCase implements UseCase<AddDishToCartRequest, AddDishToCartResponse> {

    private final Repository<User, UUID> userRepository;
    private final Repository<Dish, UUID> dishRepository;
    private final CartRepository cartRepository;


    public AddDishToCartUseCase(Repository<User, UUID> userRepository,
                               Repository<Dish, UUID> dishRepository,
                               CartRepository cartRepository) {
        this.userRepository = userRepository;
        this.dishRepository = dishRepository;
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

        } catch (EntityNotFoundException e) {
            return createErrorResponse("Entité non trouvée: " + e.getMessage());
        } catch (ValidationException e) {
            return createErrorResponse("Erreur de validation: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Erreur inattendue: " + e.getMessage());
        }
    }


    private User validateAndGetUser(UUID userId) {
        return userRepository.findById(userId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Utilisateur non trouvé avec l'ID: " + userId));
    }

    private Dish validateAndGetDish(UUID dishId) {
        Dish dish = dishRepository.findById(dishId)
            .orElseThrow(() -> new EntityNotFoundException(
                "Plat non trouvé avec l'ID: " + dishId));

        if (!dish.isAvailable()) {
            throw new ValidationException(
                "Le plat '" + dish.getName() + "' n'est pas disponible actuellement");
        }

        return dish;
    }


    private Cart getOrCreateActiveCart(UUID userId) {
        return cartRepository.findActiveCartByUserId(userId)
            .orElseGet(() -> new Cart(userId));
    }


    private AddDishToCartResponse createErrorResponse(String errorMessage) {
        return new AddDishToCartResponse(
            null,
            0,
            java.math.BigDecimal.ZERO,
            false
        );
    }
}
