package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

import java.util.UUID;

/**
 * Use case pour modifier la quantité d'un article dans le panier.
 *
 * <p>Ce use case permet de modifier la quantité d'un plat déjà présent
 * dans le panier, ou de le supprimer si la quantité est 0 ou négative.</p>
 *
 * <h3>Fonctionnalités:</h3>
 * <ul>
 *     <li>Modification de la quantité d'un article existant</li>
 *     <li>Suppression automatique si quantité ≤ 0</li>
 *     <li>Recalcul automatique du total</li>
 *     <li>Validation des règles métier</li>
 * </ul>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class UpdateCartItemUseCase implements UseCase<UpdateCartItemRequest, AddDishToCartResponse> {

    private final CartRepository cartRepository;

    public UpdateCartItemUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public AddDishToCartResponse execute(UpdateCartItemRequest request) {
        try {
            Cart cart = cartRepository.findActiveCartByUserId(request.userId())
                .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun panier actif trouvé pour l'utilisateur: " + request.userId()));

            if (request.newQuantity() <= 0) {
                cart.removeDish(request.dishId());
            } else {
                cart.updateQuantity(request.dishId(), request.newQuantity());
            }

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
