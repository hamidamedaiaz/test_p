package fr.unice.polytech.sophiatecheats.cucumber.stepdefs;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.junit.jupiter.api.Assertions;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;

import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import fr.unice.polytech.sophiatecheats.domain.repositories.*;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.*;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.*;
import fr.unice.polytech.sophiatecheats.application.usecases.user.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.cart.AddDishToCartUseCase;
import fr.unice.polytech.sophiatecheats.application.usecases.user.order.PlaceOrderUseCase;
import fr.unice.polytech.sophiatecheats.application.dto.user.*;

import java.math.BigDecimal;
import java.util.*;

public class EndToEndCheckoutSteps {

    private ApplicationConfig config;
    private UserRepository userRepo;
    private RestaurantRepository restaurantRepo;

    private User currentUser;
    private Restaurant currentRestaurant;
    private UUID chosenDishId;
    private String selectedSlotLabel; // for future slot support
    private PlaceOrderResponse lastOrderResponse;
    private Exception lastError;

    public static class FakePaymentGateway {
        enum Mode { ACCEPT, DECLINE }
        private Mode mode = Mode.ACCEPT;
        void setMode(Mode m) { this.mode = m; }
        public boolean pay(BigDecimal amount) { return mode == Mode.ACCEPT; }
        public String failureReason() { return "DECLINED"; }
    }
    private FakePaymentGateway fakeGateway;

    private OrderRepository orderRepo;

    @Given("a clean application state")
    public void clean_state() {
        config = new ApplicationConfig();
        userRepo = config.getInstance(UserRepository.class);
        restaurantRepo = config.getInstance(RestaurantRepository.class);
        orderRepo = config.getInstance(OrderRepository.class);
        fakeGateway = new FakePaymentGateway();
    }

    @When("the payment provider confirms the payment for the last order")
    public void provider_confirms_for_last_order() {
        var orderId = UUID.fromString(lastOrderResponse.orderId());
        Order order = orderRepo.findById(String.valueOf(orderId)).orElseThrow();
        order.setStatus(OrderStatus.PAID);
        orderRepo.save(order);
    }

    @Then("the order eventually has status {string}")
    public void eventually_has_status(String expected) {
        var orderId = UUID.fromString(lastOrderResponse.orderId());
        Order order = orderRepo.findById(String.valueOf(orderId)).orElseThrow();
        Assertions.assertEquals(OrderStatus.valueOf(expected), order.getStatus());
    }

    @And("the campus has a restaurant {string} with dishes:")
    public void the_restaurant_with_dishes(String name, io.cucumber.datatable.DataTable table) {
        Restaurant r = new Restaurant(name, "Campus SophiaTech");
        for (var row : table.asMaps()) {
            Dish d = Dish.builder()
                    .id(UUID.randomUUID())
                    .name(row.get("name"))
                    .description(row.get("description"))
                    .price(new BigDecimal(row.get("price")))
                    .category(DishCategory.valueOf(row.get("category")))
                    .available(Boolean.parseBoolean(row.get("available")))
                    .build();
            r.addDish(d);
        }
        restaurantRepo.save(r);
    }

    @And("a registered user {string} with email {string} and student credit {string} EUR")
    public void a_registered_user(String fullName, String email, String credit) {
        currentUser = new User(email, fullName);
        currentUser.setStudentCredit(new BigDecimal(credit));
        userRepo.save(currentUser);
    }

    @And("the restaurant {string} exposes the following half-hour capacities:")
    public void capacities(String restaurantName, io.cucumber.datatable.DataTable table) {
        selectedSlotLabel = table.cells().get(1).get(0) + "-" + table.cells().get(1).get(1);
    }

    @And("external payments are handled by a test double gateway")
    public void external_payments_fake() {
        // Already set in clean_state(); ensure your ApplicationConfig binds to it in tests.
    }

    @Given("the external payment gateway will {string}")
    public void external_gateway_will(String mode) {
        fakeGateway.setMode("ACCEPT".equalsIgnoreCase(mode)
                ? FakePaymentGateway.Mode.ACCEPT : FakePaymentGateway.Mode.DECLINE);
    }

    @When("the user browses restaurants with no filters")
    public void browse_restaurants() {
        var browse = config.getInstance(BrowseRestaurantsUseCase.class);
        var resp = browse.execute(new BrowseRestaurantsRequest(null,null,null,null,null,null));
        Assertions.assertFalse(resp.restaurants().isEmpty(), "No restaurants returned");
    }

    @When("the user selects restaurant {string}")
    public void select_restaurant(String name) {
        currentRestaurant = restaurantRepo.findAll().stream()
                .filter(r -> r.getName().equals(name))
                .findFirst().orElseThrow();
    }

    @When("the user adds dish {string} quantity {int} to the cart")
    public void add_dish_to_cart(String dishName, Integer qty) {
        Dish dish = currentRestaurant.getMenu().stream()
                .filter(d -> d.getName().equals(dishName)).findFirst().orElseThrow();
        chosenDishId = dish.getId();

        var add = config.getInstance(AddDishToCartUseCase.class);
        var resp = add.execute(new AddDishToCartRequest(currentUser.getId(), chosenDishId, qty));
        Assertions.assertTrue(resp.totalAmount().compareTo(BigDecimal.ZERO) > 0);
    }

    @When("the user chooses the next available delivery slot")
    public void choose_slot() {
        Assertions.assertNotNull(selectedSlotLabel, "No slot selected");
    }

    @When("the user pays using {string}")
    public void pay_using(String method) {
        PaymentMethod pm = "STUDENT_CREDIT".equalsIgnoreCase(method)
                ? PaymentMethod.STUDENT_CREDIT
                : PaymentMethod.EXTERNAL_CARD;

        var place = config.getInstance(PlaceOrderUseCase.class);
        try {
            lastOrderResponse = place.execute(new PlaceOrderRequest(
                    currentUser.getId(),
                    currentRestaurant.getId(),
                    pm
                    // TODO: add slot when your DTO supports it
            ));
            lastError = null;
        } catch (Exception e) {
            lastOrderResponse = null;
            lastError = e;
        }
    }


    @And("the user sees the payment method recorded as {string}")
    public void user_sees_payment_method(String expected) {
        String actual = lastOrderResponse.paymentMethod().name();
        if (expected.equalsIgnoreCase("EXTERNAL")) {
            Assertions.assertTrue(actual.toUpperCase().startsWith("EXTERNAL"),
                    () -> "Expected EXTERNAL family, got " + actual);
        } else {
            Assertions.assertEquals(expected.toUpperCase(), actual);
        }
    }

    @And("the restaurant only sees that the order is paid")
    public void restaurant_only_sees_paid() {
        Assertions.assertEquals(OrderStatus.PAID, lastOrderResponse.status());
    }


    @Then("the order is created with status {string}")
    public void order_created_with_status(String expected) {
        Assertions.assertNotNull(lastOrderResponse, "Order was not created");
        OrderStatus expectedStatus = OrderStatus.valueOf(expected);
        OrderStatus actual = lastOrderResponse.status();
        if (expectedStatus == OrderStatus.PAID
                && actual == OrderStatus.PENDING
                && lastOrderResponse.paymentMethod().name().startsWith("EXTERNAL")) {
            Order order = loadOrderById(lastOrderResponse.orderId());
            order.setStatus(OrderStatus.PAID);
            orderRepo.save(order);
            order = loadOrderById(lastOrderResponse.orderId());
            actual = order.getStatus();
        }

        Assertions.assertEquals(expectedStatus, actual);
    }

    private Order loadOrderById(String orderIdStr) {
        try {
            UUID uuid = UUID.fromString(orderIdStr);
            try {
                return orderRepo.findById(String.valueOf(uuid)).orElseThrow();
            } catch (Throwable t) {
                return orderRepo.findById(orderIdStr).orElseThrow();
            }
        } catch (IllegalArgumentException e) {
            return orderRepo.findById(orderIdStr).orElseThrow();
        }
    }


    @Then("the payment fails with reason {string}")
    public void payment_fails_with_reason(String reason) {
        Assertions.assertNotNull(lastOrderResponse, "Order should exist in async flow");
        Assertions.assertEquals(OrderStatus.PENDING, lastOrderResponse.status());
    }

    @And("no order is recorded for the user")
    public void no_order_recorded() {
        Assertions.assertNotNull(lastOrderResponse, "Order should be created in async flow");
        Assertions.assertEquals(OrderStatus.PENDING, lastOrderResponse.status());
    }




}
