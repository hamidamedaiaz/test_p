package fr.unice.polytech.sophiatecheats;

import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.AddDishToCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.GetCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Démonstration complète du flux utilisateur de bout en bout dans SophiaTechEats.
 * Montre toutes les étapes qu'un utilisateur peut réaliser :
 * navigation → ajout au panier → commande → paiement → confirmation
 */
public class EndToEndUserFlowDemo {

    public static void main(String[] args) {
        System.out.println("=== DÉMONSTRATION FLUX UTILISATEUR COMPLET - SophiaTechEats ===\n");

        // Configuration de l'application
        ApplicationConfig config = new ApplicationConfig();

        // === ÉTAPE 0: PRÉPARATION DES DONNÉES ===
        System.out.println("🔧 PRÉPARATION: Configuration des données de test...");
        setupTestData(config);

        // === ÉTAPE 1: PARCOURIR LES RESTAURANTS ===
        System.out.println("\n📋 ÉTAPE 1: L'utilisateur parcourt les restaurants disponibles");
        browseRestaurants(config);

        // === ÉTAPE 2: AJOUTER DES PLATS AU PANIER ===
        System.out.println("\n🛒 ÉTAPE 2: L'utilisateur ajoute des plats à son panier");
        addDishesToCart(config);

        // === ÉTAPE 3: CONSULTER LE PANIER ===
        System.out.println("\n👀 ÉTAPE 3: L'utilisateur consulte son panier");
        viewCart(config);

        // === ÉTAPE 4: PASSER COMMANDE AVEC PAIEMENT PAR CRÉDIT ÉTUDIANT ===
        System.out.println("\n💳 ÉTAPE 4: L'utilisateur passe commande et paie avec son crédit étudiant");
        String orderId = placeOrderWithStudentCredit(config);

        // === ÉTAPE 5: CONFIRMER LA COMMANDE ===
        System.out.println("\n✅ ÉTAPE 5: La commande est confirmée (simulation restaurant)");
        confirmOrder(config, orderId);

        System.out.println("\n🎉 === FLUX COMPLET TERMINÉ AVEC SUCCÈS ===");
        System.out.println("L'utilisateur a pu réaliser un parcours complet de A à Z !");
    }

    private static void setupTestData(ApplicationConfig config) {
        UserRepository userRepo = config.getInstance(UserRepository.class);
        RestaurantRepository restaurantRepo = config.getInstance(RestaurantRepository.class);

        // Créer un utilisateur avec du crédit
        User student = new User("etudiant.demo@unice.fr", "Marie Dupont");
        student.setStudentCredit(BigDecimal.valueOf(50.00));
        userRepo.save(student);

        // Créer un restaurant avec des plats
        Restaurant pizzeria = new Restaurant("Pizzeria SophiaTech", "Campus SophiaTech, Bâtiment Forum");
        Dish pizza = Dish.builder()
            .id(UUID.randomUUID())
            .name("Pizza Margherita")
            .description("Pizza classique avec tomate, mozzarella et basilic")
            .price(BigDecimal.valueOf(12.50))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();
        Dish salade = Dish.builder()
            .id(UUID.randomUUID())
            .name("Salade César")
            .description("Salade fraîche avec poulet grillé et parmesan")
            .price(BigDecimal.valueOf(9.00))
            .category(DishCategory.STARTER)
            .available(true)
            .build();
        Dish tiramisu = Dish.builder()
            .id(UUID.randomUUID())
            .name("Tiramisu")
            .description("Dessert italien traditionnel")
            .price(BigDecimal.valueOf(6.50))
            .category(DishCategory.DESSERT)
            .available(true)
            .build();
        pizzeria.addDish(pizza);
        pizzeria.addDish(salade);
        pizzeria.addDish(tiramisu);
        restaurantRepo.save(pizzeria);

        System.out.println("✓ Utilisateur créé: " + student.getName() + " (Crédit: " + student.getStudentCredit() + "€)");
        System.out.println("✓ Restaurant créé: " + pizzeria.getName() + " avec " + pizzeria.getMenu().size() + " plats");
    }

    private static void browseRestaurants(ApplicationConfig config) {
        BrowseRestaurantsUseCase browseUseCase = config.getInstance(BrowseRestaurantsUseCase.class);

        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(
            null, // cuisineType
            null, // availabilityFilter
            null, // dietType
            null, // minPrice
            null, // maxPrice
            null  // restaurantType
        );
        BrowseRestaurantsResponse response = browseUseCase.execute(request);

        System.out.println("📍 Restaurants disponibles: " + response.restaurants().size());
        response.restaurants().forEach(restaurant -> {
            System.out.println("  • " + restaurant.name() + " - " + restaurant.address());
            System.out.println("    💰 Plats disponibles: " + restaurant.dishes().size());
            restaurant.dishes().forEach(dish -> {
                System.out.println("      - " + dish.name() + " (" + dish.price() + "€) - " + dish.description());
            });
        });
    }

    private static void addDishesToCart(ApplicationConfig config) {
        UserRepository userRepo = config.getInstance(UserRepository.class);
        RestaurantRepository restaurantRepo = config.getInstance(RestaurantRepository.class);
        AddDishToCartUseCase addToCartUseCase = config.getInstance(AddDishToCartUseCase.class);

        User user = userRepo.findAll().get(0);
        Restaurant restaurant = restaurantRepo.findAll().get(0);

        // Ajouter pizza au panier
        Dish pizza = restaurant.getMenu().stream()
            .filter(dish -> dish.getName().contains("Pizza"))
            .findFirst().orElseThrow();

        AddDishToCartRequest pizzaRequest = new AddDishToCartRequest(user.getId(), pizza.getId(), 1);
        AddDishToCartResponse pizzaResponse = addToCartUseCase.execute(pizzaRequest);

        System.out.println("🍕 Ajouté au panier: " + pizza.getName() + " x1");
        System.out.println("   💰 Sous-total: " + pizzaResponse.totalAmount() + "€");

        // Ajouter salade au panier
        Dish salade = restaurant.getMenu().stream()
            .filter(dish -> dish.getName().contains("Salade"))
            .findFirst().orElseThrow();

        AddDishToCartRequest saladeRequest = new AddDishToCartRequest(user.getId(), salade.getId(), 1);
        AddDishToCartResponse saladeResponse = addToCartUseCase.execute(saladeRequest);

        System.out.println("🥗 Ajouté au panier: " + salade.getName() + " x1");
        System.out.println("   💰 Sous-total: " + saladeResponse.totalAmount() + "€");
    }

    private static void viewCart(ApplicationConfig config) {
        UserRepository userRepo = config.getInstance(UserRepository.class);
        GetCartUseCase getCartUseCase = config.getInstance(GetCartUseCase.class);

        User user = userRepo.findAll().get(0);

        try {
            var cartResponse = getCartUseCase.execute(user.getId());
            System.out.println("🛒 Contenu du panier:");
            System.out.println("   📦 Articles: " + cartResponse.totalItems());
            System.out.println("   💰 Total: " + cartResponse.totalAmount() + "€");

            cartResponse.items().forEach(item -> {
                System.out.println("     • " + item.dishName() + " x" + item.quantity() +
                                 " = " + item.subtotal() + "€");
            });
        } catch (Exception e) {
            System.out.println("❗ Impossible de récupérer le panier: " + e.getMessage());
        }
    }

    private static String placeOrderWithStudentCredit(ApplicationConfig config) {
        UserRepository userRepo = config.getInstance(UserRepository.class);
        RestaurantRepository restaurantRepo = config.getInstance(RestaurantRepository.class);
        PlaceOrderUseCase placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);

        User user = userRepo.findAll().get(0);
        Restaurant restaurant = restaurantRepo.findAll().get(0);

        System.out.println("💳 Crédit disponible avant commande: " + user.getStudentCredit() + "€");

        PlaceOrderRequest orderRequest = new PlaceOrderRequest(
            user.getId(),
            restaurant.getId(),
            PaymentMethod.STUDENT_CREDIT
        );

        PlaceOrderResponse orderResponse = placeOrderUseCase.execute(orderRequest);

        System.out.println("🎯 Commande créée avec succès!");
        System.out.println("   📋 ID commande: " + orderResponse.orderId());
        System.out.println("   👤 Client: " + orderResponse.customerName());
        System.out.println("   🏪 Restaurant: " + orderResponse.restaurantName());
        System.out.println("   💰 Montant: " + orderResponse.totalAmount() + "€");
        System.out.println("   💳 Paiement: " + orderResponse.paymentMethod());
        System.out.println("   📊 Statut: " + orderResponse.status());
        System.out.println("   🕐 Commandé le: " + orderResponse.orderDateTime());

        // Vérifier le crédit restant
        User updatedUser = userRepo.findById(user.getId()).orElseThrow();
        System.out.println("💳 Crédit restant: " + updatedUser.getStudentCredit() + "€");

        return orderResponse.orderId();
    }

    private static void confirmOrder(ApplicationConfig config, String orderId) {
        ConfirmOrderUseCase confirmUseCase = config.getInstance(ConfirmOrderUseCase.class);

        ConfirmOrderRequest confirmRequest = new ConfirmOrderRequest(orderId);
        ConfirmOrderResponse confirmResponse = confirmUseCase.execute(confirmRequest);

        System.out.println("✅ Commande confirmée!");
        System.out.println("   📋 ID commande: " + confirmResponse.orderId());
        System.out.println("   👤 Client: " + confirmResponse.customerName());
        System.out.println("   🏪 Restaurant: " + confirmResponse.restaurantName());
        System.out.println("   💰 Montant total: " + confirmResponse.totalAmount() + "€");
        System.out.println("   📊 Statut: " + confirmResponse.status());
        System.out.println("   ✅ Confirmée le: " + confirmResponse.confirmedAt());
        System.out.println("   🚚 Livraison estimée: " + confirmResponse.estimatedDeliveryTime());

        System.out.println("\n🍕 Votre commande sera livrée dans environ 30 minutes!");
    }
}
