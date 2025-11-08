package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implémentation en mémoire du repository de panier.
 *
 * <p>Cette implémentation stocke les paniers en mémoire avec gestion
 * des sessions et nettoyage automatique des paniers expirés.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class InMemoryCartRepository extends InMemoryRepository<Cart, UUID> implements CartRepository {

    private final Map<UUID, UUID> userCartMapping;
    private final Map<UUID, LocalDateTime> cartLastActivity;
    private static final int EXPIRY_MINUTES = 5;  // Panier expire après 5 minutes d'inactivité


    public InMemoryCartRepository() {
        super();
        this.userCartMapping = new ConcurrentHashMap<>();
        this.cartLastActivity = new ConcurrentHashMap<>();
    }

    @Override
    public Optional<Cart> findActiveCartByUserId(UUID userId) {
        if (userId == null) {
            return Optional.empty();
        }

        UUID cartId = userCartMapping.get(userId);
        if (cartId == null) {
            return Optional.empty();
        }

        Optional<Cart> cart = findById(cartId);
        if (cart.isPresent()) {
            // Mettre à jour l'activité
            cartLastActivity.put(cartId, LocalDateTime.now());
        }

        return cart;
    }

    @Override
    protected UUID extractId(Cart entity) {
        return entity != null ? entity.getId() : null;
    }

    @Override
    public Cart save(Cart cart) {
        if (cart == null) {
            return null;
        }

        // Sauvegarder le panier
        Cart savedCart = super.save(cart);

        // Maintenir le mapping utilisateur -> panier
        userCartMapping.put(cart.getUserId(), cart.getId());

        // Mettre à jour l'activité
        cartLastActivity.put(cart.getId(), LocalDateTime.now());

        return savedCart;
    }

    @Override
    public boolean deleteById(UUID cartId) {
        if (cartId == null) {
            return false;
        }
        Optional<Cart> cart = findById(cartId);
        if (cart.isPresent()) {
            userCartMapping.remove(cart.get().getUserId());
            cartLastActivity.remove(cartId);
            return super.deleteById(cartId);
        }
        return false;
    }

    @Override
    public int deleteExpiredCarts() {
        LocalDateTime expiryThreshold = LocalDateTime.now().minusMinutes(EXPIRY_MINUTES);
        int deletedCount = 0;

        // Trouver les paniers expirés
        var expiredCarts = cartLastActivity.entrySet().stream()
            .filter(entry -> entry.getValue().isBefore(expiryThreshold))
            .map(Map.Entry::getKey)
            .toList();

        // Supprimer les paniers expirés
        for (UUID cartId : expiredCarts) {
            deleteById(cartId);
            deletedCount++;
        }

        return deletedCount;
    }

    @Override
    public boolean hasActiveCart(UUID userId) {
        return findActiveCartByUserId(userId).isPresent();
    }

    @Override
    public void delete(Cart cart) {
        if (cart != null && cart.getId() != null) {
            deleteById(cart.getId());
        }
    }

}
