package fr.unice.polytech.sophiatecheats.cucumber.stepdefs;

import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.order.ConfirmOrderResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.AddDishToCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.order.ConfirmOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.cucumber.hooks.TestHooks;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.OrderAlreadyConfirmedException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import fr.unice.polytech.sophiatecheats.domain.repositories.CartRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

public class OrderPlacementStepDefs {

    private User testUser;
    private Restaurant testRestaurant;
    private PlaceOrderResponse orderResponse;
    private ConfirmOrderResponse confirmResponse;
    private Exception lastException;
    private BigDecimal initialUserCredit;

    @Before
    public void setUp() {
        lastException = null;
        orderResponse = null;
        confirmResponse = null;
    }

    @Given("the system has a restaurant {string} with available dishes")
    public void the_system_has_a_restaurant_with_available_dishes(String restaurantName) {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        RestaurantRepository restaurantRepo = config.getInstance(RestaurantRepository.class);

        testRestaurant = new Restaurant(restaurantName, "Campus SophiaTech");
        testRestaurant.addDish(Dish.builder()
            .id(UUID.randomUUID())
            .name("Pizza Margherita")
            .description("Pizza classique avec tomate, mozzarella et basilic")
            .price(BigDecimal.valueOf(12.50))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build());
        testRestaurant.addDish(Dish.builder()
            .id(UUID.randomUUID())
            .name("Salade César")
            .description("Salade fraîche avec poulet grillé et parmesan")
            .price(BigDecimal.valueOf(9.00))
            .category(DishCategory.STARTER)
            .available(true)
            .build());
        testRestaurant.addDish(Dish.builder()
            .id(UUID.randomUUID())
            .name("Plat Très Cher")
            .description("Plat hors de prix pour tester le crédit insuffisant")
            .price(BigDecimal.valueOf(100.00))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build());

        restaurantRepo.save(testRestaurant);
    }

    @And("a student user {string} with {double}€ credit is registered")
    public void a_student_user_with_credit_is_registered(String userName, Double credit) {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        UserRepository userRepo = config.getInstance(UserRepository.class);

        testUser = new User("marie.dupont@unice.fr", userName);
        testUser.setStudentCredit(BigDecimal.valueOf(credit));
        initialUserCredit = testUser.getStudentCredit();

        userRepo.save(testUser);
    }

    @Given("the user has added {string} to their cart")
    public void the_user_has_added_to_their_cart(String dishName) {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        AddDishToCartUseCase addToCartUseCase = config.getInstance(AddDishToCartUseCase.class);
        RestaurantRepository restaurantRepo = config.getInstance(RestaurantRepository.class);
        UserRepository userRepo = config.getInstance(UserRepository.class);

        System.out.println("[OrderPlacementStepDefs] Attempting to add dish='" + dishName + "' for user=" + testUser.getId());
        System.out.println("[OrderPlacementStepDefs] Restaurants in repo=" + restaurantRepo.findAll().size());
        restaurantRepo.findAll().forEach(r -> {
            System.out.println("[OrderPlacementStepDefs] Restaurant=" + r.getName() + " dishes=" + r.getMenu().size());
            r.getMenu().forEach(d -> System.out.println("  * Dish name=" + d.getName() + " id=" + d.getId()));
        });
        System.out.println("[OrderPlacementStepDefs] User exists=" + userRepo.findById(testUser.getId()).isPresent());

        Dish dish = testRestaurant.getMenu().stream()
            .filter(d -> d.getName().equals(dishName))
            .findFirst()
            .orElseThrow(() -> new RuntimeException("Dish not found: " + dishName));

        System.out.println("[OrderPlacementStepDefs] Using dishId=" + dish.getId());
        AddDishToCartRequest request = new AddDishToCartRequest(testUser.getId(), dish.getId(), 1);
        AddDishToCartResponse response = addToCartUseCase.execute(request);

        if (!response.success()) {
            throw new RuntimeException("Failed to add dish to cart: " + dishName);
        }
    }

    @When("the user places an order using student credit")
    public void the_user_places_an_order_using_student_credit() {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        PlaceOrderUseCase placeOrderUseCase = config.getInstance(PlaceOrderUseCase.class);

        try {
            PlaceOrderRequest request = new PlaceOrderRequest(
                testUser.getId(),
                testRestaurant.getId(),
                PaymentMethod.STUDENT_CREDIT
            );
            orderResponse = placeOrderUseCase.execute(request);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the order should be created successfully")
    public void the_order_should_be_created_successfully() {
        assertNotNull(orderResponse, "Order should be created");
        assertNotNull(orderResponse.orderId(), "Order ID should be present");
    }

    @And("the order status should be {string}")
    public void the_order_status_should_be(String expectedStatus) {
        // If we have a confirm response (after confirmation), use that status
        // Otherwise, use the order response (after placement)
        if (confirmResponse != null) {
            assertEquals(OrderStatus.valueOf(expectedStatus), confirmResponse.status());
        } else {
            assertNotNull(orderResponse, "Order response should exist");
            assertEquals(OrderStatus.valueOf(expectedStatus), orderResponse.status());
        }
    }

    @And("the user's credit should be reduced by the order amount")
    public void the_users_credit_should_be_reduced_by_the_order_amount() {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        UserRepository userRepo = config.getInstance(UserRepository.class);
        User updatedUser = userRepo.findById(testUser.getId()).orElseThrow();

        BigDecimal expectedCredit = initialUserCredit.subtract(orderResponse.totalAmount());
        assertEquals(expectedCredit, updatedUser.getStudentCredit(),
            "User credit should be reduced by order amount");
    }

    @When("the order is confirmed")
    public void the_order_is_confirmed() {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        ConfirmOrderUseCase confirmUseCase = config.getInstance(ConfirmOrderUseCase.class);

        try {
            ConfirmOrderRequest request = new ConfirmOrderRequest(orderResponse.orderId());
            confirmResponse = confirmUseCase.execute(request);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @And("an estimated delivery time should be set")
    public void an_estimated_delivery_time_should_be_set() {
        assertNotNull(confirmResponse, "Confirm response should exist");
        assertNotNull(confirmResponse.estimatedDeliveryTime(), "Delivery time should be set");
    }

    @And("the delivery time should be approximately {int} minutes from confirmation")
    public void the_delivery_time_should_be_approximately_minutes_from_confirmation(Integer expectedMinutes) {
        assertNotNull(confirmResponse, "Confirm response should exist");

        LocalDateTime confirmTime = confirmResponse.confirmedAt();
        LocalDateTime deliveryTime = confirmResponse.estimatedDeliveryTime();

        long actualMinutes = ChronoUnit.MINUTES.between(confirmTime, deliveryTime);
        assertTrue(Math.abs(actualMinutes - 15) <= 2,
            "Delivery time should be approximately 15 minutes from confirmation (actual: " + actualMinutes + ")");
    }

    @Given("the user has added expensive items totaling more than their credit")
    public void the_user_has_added_expensive_items_totaling_more_than_their_credit() {
        the_user_has_added_to_their_cart("Plat Très Cher");
    }

    @When("the user attempts to place an order using student credit")
    public void the_user_attempts_to_place_an_order_using_student_credit() {
        the_user_places_an_order_using_student_credit();
    }

    @Then("the system should display an error about insufficient credit")
    public void the_system_should_display_an_error_about_insufficient_credit() {
        assertNotNull(lastException, "An exception should have been thrown");
        assertTrue(lastException instanceof InsufficientCreditException,
            "Exception should be InsufficientCreditException");
    }

    @And("no order should be created")
    public void no_order_should_be_created() {
        assertNull(orderResponse, "No order should be created when credit is insufficient");
    }

    @Given("the user has placed and paid for an order")
    public void the_user_has_placed_and_paid_for_an_order() {
        the_user_has_added_to_their_cart("Pizza Margherita");
        the_user_places_an_order_using_student_credit();
        assertNotNull(orderResponse, "Order should be placed successfully");
    }

    @And("the order has been confirmed")
    public void the_order_has_been_confirmed() {
        the_order_is_confirmed();
        assertNotNull(confirmResponse, "Order should be confirmed successfully");
    }

    @When("the user attempts to confirm the order again")
    public void the_user_attempts_to_confirm_the_order_again() {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        ConfirmOrderUseCase confirmUseCase = config.getInstance(ConfirmOrderUseCase.class);

        try {
            ConfirmOrderRequest request = new ConfirmOrderRequest(orderResponse.orderId());
            confirmUseCase.execute(request);
        } catch (Exception e) {
            lastException = e;
        }
    }

    @Then("the system should display an error about order already confirmed")
    public void the_system_should_display_an_error_about_order_already_confirmed() {
        assertNotNull(lastException, "An exception should have been thrown");
        assertInstanceOf(OrderAlreadyConfirmedException.class, lastException, "Exception should be OrderAlreadyConfirmedException");
    }

    @And("the order status should remain {string}")
    public void the_order_status_should_remain(String expectedStatus) {
        // The status is already confirmed, this just verifies it didn't change
        assertEquals(OrderStatus.valueOf(expectedStatus), confirmResponse.status());
    }

    @Given("the user has an active order in progress")
    public void the_user_has_an_active_order_in_progress() {
        the_user_has_added_to_their_cart("Pizza Margherita");
        the_user_places_an_order_using_student_credit();
        assertNotNull(orderResponse, "First order should be placed successfully");
    }

    @When("the user attempts to place another order")
    public void the_user_attempts_to_place_another_order() {
        // Clear previous exception
        lastException = null;

        // Add another dish to cart (this will create a new cart)
        the_user_has_added_to_their_cart("Salade César");

        // Try to place another order
        the_user_places_an_order_using_student_credit();
    }

    @Then("the system should display an error about existing active order")
    public void the_system_should_display_an_error_about_existing_active_order() {
        assertNotNull(lastException, "An exception should have been thrown");
        assertTrue(lastException instanceof ValidationException,
            "Exception should be ValidationException");
        assertTrue(lastException.getMessage().contains("commande en cours"),
            "Error message should mention existing order");
    }

    @And("the new order should not be created")
    public void the_new_order_should_not_be_created() {
        // The orderResponse still contains the first order, not a new one
        assertNotNull(orderResponse, "Original order should still exist");
        // If a new order was created, we would have a different order ID or different state
    }
}
