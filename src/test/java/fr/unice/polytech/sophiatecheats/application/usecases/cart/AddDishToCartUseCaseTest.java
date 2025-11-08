package fr.unice.polytech.sophiatecheats.application.usecases.cart;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Tests unitaires pour AddDishToCartUseCase.
 *
 * <p>Ces tests valident le comportement du use case pour ajouter un plat au panier
 * et vérifier que le total est correctement calculé.</p>
 *
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
class AddDishToCartUseCaseTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private CartRepository cartRepository;

    private AddDishToCartUseCase useCase;

    private UUID userId;
    private UUID dishId;
    private User testUser;
    private Dish testDish;
    private Restaurant testRestaurant;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        useCase = new AddDishToCartUseCase(userRepository, restaurantRepository, cartRepository);

        userId = UUID.randomUUID();
        testUser = new User("marcel@example.com", "Marcel Dupont");
        testDish = Dish.builder()
                .name("Tacos 3 viandes")
                .description("Délicieux tacos avec 3 viandes")
                .price(BigDecimal.valueOf(8.50))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();
        dishId = testDish.getId(); // capture generated id

        testRestaurant = new Restaurant("Test Restaurant", "Test Address");
        testRestaurant.addDish(testDish);
    }

    /**
     * Test du scénario nominal : ajouter un plat au panier et voir le total.
     *
     * <p>Scénario testé:
     * - Marcel veut ajouter 2 tacos à son panier
     * - Le système crée un nouveau panier
     * - Le total est calculé automatiquement (8.50 × 2 = 17.00€)
     * - La réponse confirme le succès avec le bon total</p>
     */
    @Test
    void should_add_dish_to_cart_and_calculate_total_successfully() {
        // Given - Préparation du scénario
        AddDishToCartRequest request = new AddDishToCartRequest(userId, dishId, 2);

        // Configuration des mocks
        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(testRestaurant));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.empty()); // Pas de panier existant
        when(cartRepository.save(any(Cart.class))).thenReturn(null);

        // When - Exécution du use case
        AddDishToCartResponse response = useCase.execute(request);

        // Then - Vérifications
        assertTrue(response.success(), "L'ajout au panier devrait réussir");
        assertNotNull(response.cartId(), "Un ID de panier devrait être généré");
        assertEquals(2, response.totalItems(), "Le panier devrait contenir 2 articles");
        assertEquals(BigDecimal.valueOf(17.0), response.totalAmount(), "Le total devrait être 17.00€ (8.50 × 2)");

        // Vérification des interactions
        verify(userRepository).findById(userId);
        verify(restaurantRepository, times(2)).findAll();
        verify(cartRepository).findActiveCartByUserId(userId);
        verify(cartRepository).save(any(Cart.class));
    }

    /**
     * Test d'ajout à un panier existant.
     *
     * <p>Scénario:
     * - Marcel a déjà un panier avec 1 tacos
     * - Il ajoute 2 tacos supplémentaires
     * - La quantité totale devient 3
     * - Le total est recalculé (8.50 × 3 = 25.50€)</p>
     */
    @Test
    void should_add_dish_to_existing_cart_and_update_total() {
        // Given
        Cart existingCart = new Cart(userId);
        existingCart.addDish(testDish, 1); // Panier existant avec 1 tacos

        AddDishToCartRequest request = new AddDishToCartRequest(userId, dishId, 2);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(testRestaurant));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.of(existingCart));
        when(cartRepository.save(any(Cart.class))).thenReturn(null);

        // When
        AddDishToCartResponse response = useCase.execute(request);

        // Then
        assertTrue(response.success());
        assertEquals(3, response.totalItems(), "Le panier devrait contenir 3 articles au total");
        assertEquals(BigDecimal.valueOf(25.5), response.totalAmount(), "Le total devrait être 25.50€ (8.50 × 3)");
    }

    /**
     * Test d'erreur : utilisateur non trouvé.
     */
    @Test
    void should_fail_when_user_not_found() {
        // Given
        AddDishToCartRequest request = new AddDishToCartRequest(userId, dishId, 1);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // When
        AddDishToCartResponse response = useCase.execute(request);

        // Then
        assertFalse(response.success());
        assertNull(response.cartId());
    }

    /**
     * Test d'erreur : plat non disponible.
     */
    @Test
    void should_fail_when_dish_not_available() {
        // Given unavailable dish added to restaurant
        Dish unavailableDish = Dish.builder()
                .id(UUID.randomUUID())
                .name("Plat indisponible")
                .description("Description")
                .price(BigDecimal.valueOf(10.0))
                .category(DishCategory.MAIN_COURSE)
                .available(false)
                .build();
        testRestaurant.addDish(unavailableDish);

        AddDishToCartRequest request = new AddDishToCartRequest(userId, unavailableDish.getId(), 1);

        when(userRepository.findById(userId)).thenReturn(Optional.of(testUser));
        when(restaurantRepository.findAll()).thenReturn(Collections.singletonList(testRestaurant));
        when(cartRepository.findActiveCartByUserId(userId)).thenReturn(Optional.empty());

        // When
        AddDishToCartResponse response = useCase.execute(request);

        // Then
        assertFalse(response.success());
        assertNull(response.cartId());
    }
}
