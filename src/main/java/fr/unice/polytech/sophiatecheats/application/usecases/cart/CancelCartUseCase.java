package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.CancelCartResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.exceptions.EntityNotFoundException;

import java.util.UUID;


public class CancelCartUseCase implements UseCase<UUID, CancelCartResponse> {

    private final CartRepository cartRepository;

    public CancelCartUseCase(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public CancelCartResponse execute(UUID userId) {
        try {
            Cart cart = cartRepository.findActiveCartByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException(
                    "Aucun panier actif trouvé pour l'utilisateur: " + userId));

            UUID cartId = cart.getId();

            // Suppression complète du panier
            cartRepository.delete(cart);

            return new CancelCartResponse(
                cartId,
                true,
                "Panier annulé et supprimé avec succès"
            );

        } catch (EntityNotFoundException e) {
            return new CancelCartResponse(
                null,
                false,
                "Panier introuvable: " + e.getMessage()
            );
        } catch (Exception e) {
            return new CancelCartResponse(
                null,
                false,
                "Erreur lors de la suppression du panier: " + e.getMessage()
            );
        }
    }
}

