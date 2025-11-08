package fr.unice.polytech.sophiatecheats.domain.entities.cart;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.CannotMixRestaurantsException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitaires pour l'entité Cart avec la règle métier
 * "Un panier ne peut contenir que des plats d'un seul restaurant".
 */
class CartTest {

    private Cart cart;
    private UUID userId;
    private UUID restaurant1Id;
    private UUID restaurant2Id;
    private Dish pizzaFromRestaurant1;
    private Dish burgerFromRestaurant2;
    private Dish tiramisuFromRestaurant1;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        restaurant1Id = UUID.randomUUID();
        restaurant2Id = UUID.randomUUID();

        cart = new Cart(userId);

        pizzaFromRestaurant1 = Dish.builder()
                .name("Pizza Margherita")
                .description("Delicious pizza")
                .price(BigDecimal.valueOf(12.50))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();

        burgerFromRestaurant2 = Dish.builder()
                .name("Burger Bio")
                .description("Organic burger")
                .price(BigDecimal.valueOf(15.00))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build();

        tiramisuFromRestaurant1 = Dish.builder()
                .name("Tiramisu")
                .description("Italian dessert")
                .price(BigDecimal.valueOf(6.50))
                .category(DishCategory.DESSERT)
                .available(true)
                .build();
    }

    @Test
    @DisplayName("Un panier vide n'a pas de restaurant associé")
    void emptyCartHasNoRestaurant() {
        assertNull(cart.getRestaurantId(), "Le panier vide ne devrait avoir aucun restaurant");
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Ajouter un plat à un panier vide définit le restaurant du panier")
    void addingFirstDishSetsRestaurantId() {
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);

        assertNotNull(cart.getRestaurantId());
        assertEquals(restaurant1Id, cart.getRestaurantId());
        assertEquals(1, cart.getTotalItems());
    }

    @Test
    @DisplayName("Peut ajouter plusieurs plats du même restaurant")
    void canAddMultipleDishesFromSameRestaurant() {
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);
        cart.addDish(tiramisuFromRestaurant1, 1, restaurant1Id);

        assertEquals(restaurant1Id, cart.getRestaurantId());
        assertEquals(2, cart.getTotalItems());
    }

    @Test
    @DisplayName("Ne peut PAS ajouter un plat d'un restaurant différent")
    void cannotAddDishFromDifferentRestaurant() {
        // Ajouter d'abord un plat du restaurant 1
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);

        // Essayer d'ajouter un plat du restaurant 2
        CannotMixRestaurantsException exception = assertThrows(
                CannotMixRestaurantsException.class,
                () -> cart.addDish(burgerFromRestaurant2, 1, restaurant2Id)
        );

        assertEquals("CANNOT_MIX_RESTAURANTS", exception.getErrorCode());
        assertEquals(1, cart.getTotalItems(), "Le panier devrait toujours contenir 1 seul plat");
        assertEquals(restaurant1Id, cart.getRestaurantId(), "Le restaurant du panier ne devrait pas changer");
    }

    @Test
    @DisplayName("Vider le panier réinitialise le restaurant")
    void clearingCartResetsRestaurant() {
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);
        assertEquals(restaurant1Id, cart.getRestaurantId());

        cart.clear();

        assertNull(cart.getRestaurantId(), "Le restaurant devrait être réinitialisé");
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("Après avoir vidé le panier, peut ajouter un plat d'un autre restaurant")
    void afterClearingCanAddDishFromAnotherRestaurant() {
        // Ajouter pizza du restaurant 1
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);
        assertEquals(restaurant1Id, cart.getRestaurantId());

        // Vider le panier
        cart.clear();

        // Ajouter burger du restaurant 2 (devrait fonctionner)
        cart.addDish(burgerFromRestaurant2, 1, restaurant2Id);

        assertEquals(restaurant2Id, cart.getRestaurantId());
        assertEquals(1, cart.getTotalItems());
    }

    @Test
    @DisplayName("Supprimer tous les plats réinitialise le restaurant")
    void removingAllItemsResetsRestaurant() {
        cart.addDish(pizzaFromRestaurant1, 2, restaurant1Id);

        cart.removeDish(pizzaFromRestaurant1.getId());

        assertNull(cart.getRestaurantId(), "Le restaurant devrait être réinitialisé");
        assertTrue(cart.isEmpty());
    }

    @Test
    @DisplayName("belongsToRestaurant retourne true pour le bon restaurant")
    void belongsToRestaurantReturnsTrueForCorrectRestaurant() {
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);

        assertTrue(cart.belongsToRestaurant(restaurant1Id));
        assertFalse(cart.belongsToRestaurant(restaurant2Id));
    }

    @Test
    @DisplayName("Lève une exception si restaurantId est null lors de l'ajout")
    void throwsExceptionIfRestaurantIdIsNull() {
        assertThrows(
                ValidationException.class,
                () -> cart.addDish(pizzaFromRestaurant1, 1, null)
        );
    }

    @Test
    @DisplayName("Peut augmenter la quantité d'un plat existant")
    void canIncreaseQuantityOfExistingDish() {
        cart.addDish(pizzaFromRestaurant1, 1, restaurant1Id);
        cart.addDish(pizzaFromRestaurant1, 2, restaurant1Id);

        assertEquals(1, cart.getItems().size(), "Devrait avoir 1 type de plat");
        assertEquals(3, cart.getTotalItems(), "Devrait avoir 3 pizzas au total");
    }
}