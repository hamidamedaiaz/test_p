package fr.unice.polytech.sophiatecheats.application.usecases.cart;


import fr.unice.polytech.sophiatecheats.application.dto.user.response.ClearCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Use case pour vider complètement le panier.
 *
 * <p>Ce use case permet de supprimer tous les articles du panier
 * en une seule opération, sans supprimer le panier lui-même.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class ClearCartUseCase implements UseCase<UUID, ClearCartResponse> {

    private final CartRepository cartRepository;

    public ClearCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public ClearCartResponse execute(UUID userId) {
        try {
            Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun panier actif trouvé pour l'utilisateur: " + userId));

            cart.clear();

            cartRepository.save(cart);

            return new ClearCartResponse(
                cart.getId(),
                0,
                BigDecimal.ZERO,
                true,
                "Panier vidé avec succès"
            );

        } catch (EntityNotFoundException e) {
            return createErrorResponse("Panier introuvable: " + e.getMessage());
        } catch (ValidationException e) {
            return createErrorResponse("Erreur de validation: " + e.getMessage());
        } catch (Exception e) {
            return createErrorResponse("Erreur inattendue: " + e.getMessage());
        }
    }

    private ClearCartResponse createErrorResponse(String errorMessage) {
        return new ClearCartResponse(
            null,
            0,
            BigDecimal.ZERO,
            false,
            errorMessage
        );
    }
}
