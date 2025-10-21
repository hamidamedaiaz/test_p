package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.RemoveFromCartRequest;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

/**
 * Use case pour supprimer un plat du panier.
 *
 * <p>Ce use case permet de supprimer complètement un plat du panier,
 * quelle que soit sa quantité actuelle.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class RemoveFromCartUseCase implements UseCase<RemoveFromCartRequest, AddDishToCartResponse> {

    private final CartRepository cartRepository;


    public RemoveFromCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public AddDishToCartResponse execute(RemoveFromCartRequest request) {
        try {
            Cart cart = cartRepository.findActiveCartByUserId(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun panier actif trouvé pour l'utilisateur: " + request.userId()));

            cart.removeDish(request.dishId());

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


    private AddDishToCartResponse createErrorResponse(String errorMessage) {
        return new AddDishToCartResponse(
            null,
            0,
            java.math.BigDecimal.ZERO,
            false
        );
    }
}
