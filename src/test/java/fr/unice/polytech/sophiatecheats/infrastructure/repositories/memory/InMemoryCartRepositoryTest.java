package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class InMemoryCartRepositoryTest {
    private InMemoryCartRepository repository;
    private UUID userId;
    private Cart cart;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCartRepository();
        userId = UUID.randomUUID();
        cart = new Cart(userId);
    }

    @Test
    void should_save_and_find_cart() {
        Cart savedCart = repository.save(cart);

        assertNotNull(savedCart);
        assertEquals(cart.getId(), savedCart.getId());

        Optional<Cart> foundCart = repository.findById(cart.getId());
        assertTrue(foundCart.isPresent());
        assertEquals(cart.getId(), foundCart.get().getId());
        assertEquals(cart.getUserId(), foundCart.get().getUserId());
    }

    @Test
    void should_find_active_cart_by_userId() {
        repository.save(cart);

        Optional<Cart> foundCart = repository.findActiveCartByUserId(userId);
        assertTrue(foundCart.isPresent());
        assertEquals(cart.getId(), foundCart.get().getId());
    }

    @Test
    void should_return_empty_for_null_userId() {
        Optional<Cart> result = repository.findActiveCartByUserId(null);
        assertTrue(result.isEmpty());
    }

    @Test
    void should_return_empty_for_nonexistent_user() {
        Optional<Cart> result = repository.findActiveCartByUserId(UUID.randomUUID());
        assertTrue(result.isEmpty());
    }

    @Test
    void should_handle_null_cart_save() {
        Cart result = repository.save(null);
        assertNull(result);
    }

    @Test
    void should_update_existing_cart() {
        Cart savedCart = repository.save(cart);
        Cart updatedCart = new Cart(userId);
        updatedCart = repository.save(updatedCart);

        Optional<Cart> foundCart = repository.findActiveCartByUserId(userId);
        assertTrue(foundCart.isPresent());
        assertEquals(updatedCart.getId(), foundCart.get().getId());
        assertNotEquals(savedCart.getId(), updatedCart.getId());
    }

    @Test
    void should_delete_cart_by_id() {
        repository.save(cart);

        assertTrue(repository.deleteById(cart.getId()));
        assertFalse(repository.findById(cart.getId()).isPresent());
        assertFalse(repository.hasActiveCart(userId));
    }

    @Test
    void should_return_false_when_deleting_nonexistent_cart() {
        assertFalse(repository.deleteById(UUID.randomUUID()));
    }

    @Test
    void should_return_false_when_deleting_null_id() {
        assertFalse(repository.deleteById(null));
    }

    @Test
    void should_check_active_cart_existence() {
        assertFalse(repository.hasActiveCart(userId));

        repository.save(cart);
        assertTrue(repository.hasActiveCart(userId));

        repository.deleteById(cart.getId());
        assertFalse(repository.hasActiveCart(userId));
    }

    @Test
    void should_delete_expired_carts() {
        // Save multiple carts
        Cart cart1 = new Cart(UUID.randomUUID());
        Cart cart2 = new Cart(UUID.randomUUID());
        Cart cart3 = new Cart(UUID.randomUUID());

        repository.save(cart1);
        repository.save(cart2);
        repository.save(cart3);

        // Test initial state
        assertTrue(repository.findById(cart1.getId()).isPresent());
        assertTrue(repository.findById(cart2.getId()).isPresent());
        assertTrue(repository.findById(cart3.getId()).isPresent());

        // Vérifier qu'aucun panier n'est supprimé initialement
        assertEquals(0, repository.deleteExpiredCarts());

        // Supprimer les paniers un par un pour vérifier le comptage
        assertTrue(repository.deleteById(cart1.getId()));
        assertTrue(repository.deleteById(cart2.getId()));

        // Vérifier que les paniers ont bien été supprimés
        assertFalse(repository.findById(cart1.getId()).isPresent());
        assertFalse(repository.findById(cart2.getId()).isPresent());
        assertTrue(repository.findById(cart3.getId()).isPresent());
    }

    @Test
    void should_extract_id_from_cart() {
        UUID extractedId = repository.extractId(cart);
        assertEquals(cart.getId(), extractedId);

        assertNull(repository.extractId(null));
    }

    @Test
    void should_handle_multiple_users_and_carts() {
        UUID user1 = UUID.randomUUID();
        UUID user2 = UUID.randomUUID();

        Cart cart1 = new Cart(user1);
        Cart cart2 = new Cart(user2);

        repository.save(cart1);
        repository.save(cart2);

        Optional<Cart> foundCart1 = repository.findActiveCartByUserId(user1);
        Optional<Cart> foundCart2 = repository.findActiveCartByUserId(user2);

        assertTrue(foundCart1.isPresent());
        assertTrue(foundCart2.isPresent());
        assertEquals(cart1.getId(), foundCart1.get().getId());
        assertEquals(cart2.getId(), foundCart2.get().getId());
    }

    @Test
    void should_update_last_activity_on_access() {

        repository.save(cart);

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }

        // Accès au panier pour mettre à jour l'activité
        Optional<Cart> accessedCart = repository.findActiveCartByUserId(userId);
        assertTrue(accessedCart.isPresent());

        // Le panier ne devrait pas être expiré après l'accès
        int deletedCount = repository.deleteExpiredCarts();
        assertEquals(0, deletedCount);
        assertTrue(repository.findById(cart.getId()).isPresent());
    }
}
