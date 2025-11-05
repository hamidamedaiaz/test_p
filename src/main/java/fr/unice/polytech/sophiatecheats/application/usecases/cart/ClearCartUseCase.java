package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

import java.util.UUID;

/**
 * Use case pour vider complètement le panier.
 *
 * <p>Ce use case permet de supprimer tous les articles du panier
 * en une seule opération.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class ClearCartUseCase implements UseCase<UUID, AddDishToCartResponse> {

    private final CartRepository cartRepository;


    public ClearCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }


    @Override
    public AddDishToCartResponse execute(UUID userId) {
        try {
            Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun panier actif trouvé pour l'utilisateur: " + userId));

            cart.clear();

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
