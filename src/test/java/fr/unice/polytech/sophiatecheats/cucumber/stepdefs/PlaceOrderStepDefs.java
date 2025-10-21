package fr.unice.polytech.sophiatecheats.cucumber.stepdefs;

import fr.unice.polytech.sophiatecheats.domain.entities.order.Order;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.*;
import fr.unice.polytech.sophiatecheats.domain.entities.user.CampusUser;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.OrderStatus;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InsufficientCreditException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.InvalidCartOperationException;
import fr.unice.polytech.sophiatecheats.domain.exceptions.SlotNotFoundException;
import fr.unice.polytech.sophiatecheats.domain.services.CartService;
import fr.unice.polytech.sophiatecheats.domain.services.OrderService;
import fr.unice.polytech.sophiatecheats.domain.services.RestaurantService;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryCartRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryOrderRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory.InMemoryRestaurantRepository;
import io.cucumber.datatable.DataTable;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class PlaceOrderStepDefs {

    private Map<String, Restaurant> restaurants = new HashMap<>();
    private Map<String, CampusUser> campusUsers = new HashMap<>();
    private OrderService orderService;
    private CartService cartService;
    private RestaurantService restaurantService;
    private Exception caughtException;
    private Order lastOrder;

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    @Given("an administrator initializes the system with a clean in-memory repository")
    public void initialize_system() {
        restaurants.clear();
        campusUsers.clear();
        InMemoryCartRepository cartRepo = new InMemoryCartRepository();
        InMemoryOrderRepository orderRepo = new InMemoryOrderRepository();
        InMemoryRestaurantRepository restaurantRepo = new InMemoryRestaurantRepository(false);

        cartService = new CartService(cartRepo);
        orderService = new OrderService(orderRepo, cartService);
        restaurantService = new RestaurantService(restaurantRepo);

        caughtException = null;
        lastOrder = null;
    }

    @Given("an administrator creates a restaurant named {string} with the following dishes:")
    public void create_restaurant_with_dishes(String restaurantName, DataTable table) {
        Restaurant restaurant = restaurantService.createRestaurant(restaurantName, "Unknown Address");

        table.asMaps().forEach(row -> {
            Dish dish = Dish.builder()
                    .name(row.get("dish"))
                    .description(row.getOrDefault("description", ""))
                    .price(BigDecimal.valueOf(Double.parseDouble(row.get("price"))))
                    .category(DishCategory.MAIN_COURSE)
                    .available(row.get("status").equalsIgnoreCase("available"))
                    .build();
            restaurant.addDish(dish);
        });

        // Génération d'un créneau pour le lendemain à midi
        LocalDateTime slotStart = LocalDateTime.now().plusDays(1).withHour(12).withMinute(0);
        LocalDateTime slotEnd = slotStart.plusHours(1);
        restaurantService.generateDeliverySlots(restaurant.getId(), slotStart.toLocalDate(), slotStart.toLocalTime(), slotEnd.toLocalTime(), 10);

        restaurants.put(restaurantName, restaurant);
        restaurantService.openRestaurant(restaurant.getId());
    }

    @Given("a restaurant {string} exists with the following dishes:")
    public void existing_restaurant_with_dishes(String restaurantName, io.cucumber.datatable.DataTable table) {
        create_restaurant_with_dishes(restaurantName, table);
    }

    @Given("a CampusUser {string} is registered with a balance of {string} euros")
    public void register_CampusUser(String campusUserName, String balanceStr) {
        double balance = Double.parseDouble(balanceStr);
        CampusUser user = CampusUser
                .builder()
                .name(campusUserName)
                .email(campusUserName + "@example.com")
                .balance(balance)
                .build();

        campusUsers.put(campusUserName, user);
    }

    @Given("CampusUser {string} adds to the cart from restaurant {string}:")
    public void campusUser_adds_to_cart(String campusUserName, String restaurantName, io.cucumber.datatable.DataTable table) {
        CampusUser user = campusUsers.get(campusUserName);
        Restaurant restaurant = restaurants.get(restaurantName);

        table.asMaps().forEach(row -> {
            Dish dish = restaurant.findDishByName(row.get("dish"))
                    .orElseThrow(() -> new RuntimeException("Dish not found: " + row.get("dish")));
            int quantity = Integer.parseInt(row.get("quantity"));
            try {
                cartService.addToCart(user, dish, quantity);
                caughtException = null;
            } catch (Exception e) {
                caughtException = e;
            }
        });
    }

    @When("CampusUser {string} places the order for the slot {string}")
    public void place_order(String campusUserName, String slotStr) {
        CampusUser user = campusUsers.get(campusUserName);
        Restaurant restaurant = restaurants.values().iterator().next();

        LocalDateTime slotTime = LocalDateTime.parse(slotStr, formatter);

        restaurantService.generateDeliverySlots(
                restaurant.getId(),
                slotTime.toLocalDate(),
                LocalTime.of(11, 0),
                LocalTime.of(14, 0),
                10
        );

        try {
            TimeSlot slot = restaurant.getDeliverySchedule().getSlotsForDate(slotTime.toLocalDate()).stream()
                    .filter(s -> s.getStartTime().equals(slotTime))
                    .findFirst()
                    .orElseThrow(() -> new SlotNotFoundException("TimeSlot not found: " + slotTime));

            lastOrder = orderService.createOrder(user, restaurant, slot);
            caughtException = null;

        } catch (Exception e) {
            caughtException = e;
            lastOrder = null;
        }
    }

    @When("CampusUser {string} proceeds to payment")
    public void proceed_to_payment(String campusUserName) {
        CampusUser user = campusUsers.get(campusUserName);
        if (lastOrder == null) {
            caughtException = new NullPointerException("No order found to pay.");
            return;
        }
        try {
            orderService.payOrder(user, lastOrder);
            caughtException = null;
        } catch (Exception e) {
            caughtException = e;
        }
    }

    @Then("the order is successfully created")
    public void order_successfully_created() {
        assertNotNull(lastOrder);
        assertNull(caughtException);
    }

    @Then("the total amount of the order is {string} euros")
    public void order_total_amount(String expectedTotalStr) {
        double expected = Double.parseDouble(expectedTotalStr);
        assertEquals(expected, lastOrder.getTotalAmount().doubleValue());
    }

    @Then("the payment is successful")
    public void payment_successful() {
        assertEquals(OrderStatus.PAID, lastOrder.getStatus());
        assertNull(caughtException);
    }

    @Then("the CampusUser balance is updated to {string} euros")
    public void campusUser_balance_updated(String expectedBalanceStr) {
        double expected = Double.parseDouble(expectedBalanceStr);
        assertEquals(expected, campusUsers.get(lastOrder.getUser().getName()).getBalance().doubleValue());
    }

    @Then("the order status is {string}")
    public void order_status(String expectedStatus) {
        assertEquals(OrderStatus.valueOf(expectedStatus.toUpperCase()), lastOrder.getStatus());
    }

    @Then("an error {string} is returned")
    public void error_is_returned(String expectedMessage) {
        assertNotNull(caughtException);
        assertTrue(caughtException.getMessage().contains(expectedMessage));
    }

    @Then("the order is not paid")
    public void order_not_paid() {
        if (lastOrder != null) {
            assertNotEquals(OrderStatus.PAID, lastOrder.getStatus());
        }
    }

    @Then("the order is not created")
    public void order_not_created() {
        assertNull(lastOrder);
    }
}
