package fr.unice.polytech.sophiatecheats.application.facade;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.AddDishToCartRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.PlaceOrderRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.AddDishToCartResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.PlaceOrderResponse;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.PaymentMethod;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.domain.repositories.UserRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class SophiaTechEatsFacadeTest {

    private SophiaTechEatsFacade facade;
    private ApplicationConfig config;
    private UserRepository userRepository;
    private RestaurantRepository restaurantRepository;

    @BeforeEach
    void setUp() {
        config = new ApplicationConfig();
        facade = new SophiaTechEatsFacade(config);

        userRepository = config.getInstance(UserRepository.class);
        restaurantRepository = config.getInstance(RestaurantRepository.class);
    }

    @Test
    void should_provide_simplified_access_to_browse_restaurants() {
        Restaurant restaurant = new Restaurant("Test Restaurant", "Campus Address");
        Dish dish = Dish.builder()
            .id(UUID.randomUUID())
            .name("Test Dish")
            .price(BigDecimal.valueOf(10.0))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();
        restaurant.addDish(dish);
        restaurantRepository.save(restaurant);

        BrowseRestaurantsResponse response = facade.browseAllRestaurants();

        assertNotNull(response);
        assertFalse(response.restaurants().isEmpty());
    }

    @Test
    void should_provide_simplified_access_to_add_dish_to_cart() {
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);

        Restaurant restaurant = new Restaurant("Test Restaurant", "Campus Address");
        Dish dish = Dish.builder()
            .id(UUID.randomUUID())
            .name("Test Dish")
            .price(BigDecimal.valueOf(10.0))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();
        restaurant.addDish(dish);
        restaurantRepository.save(restaurant);

        AddDishToCartRequest request = new AddDishToCartRequest(user.getId(), dish.getId(), 2);
        AddDishToCartResponse response = facade.addDishToCart(request);

        assertTrue(response.success());
        assertEquals(2, response.totalItems());
    }

    @Test
    void should_provide_simplified_access_to_clear_cart() {
        User user = new User("test@example.com", "Test User");
        userRepository.save(user);

        assertDoesNotThrow(() -> facade.clearCart(user.getId()));
    }

    @Test
    void should_demonstrate_facade_pattern_benefit() {
        BrowseRestaurantsResponse availableRestaurants = facade.browseAvailableRestaurants();

        assertNotNull(availableRestaurants);
    }

    @Test
    void facade_should_hide_complexity_of_multiple_use_cases() {
        User user = new User("test@example.com", "Test User");
        user.setStudentCredit(BigDecimal.valueOf(100.0));
        userRepository.save(user);

        Restaurant restaurant = new Restaurant("Test Restaurant", "Campus");
        Dish dish = Dish.builder()
            .id(UUID.randomUUID())
            .name("Pizza")
            .price(BigDecimal.valueOf(12.0))
            .category(DishCategory.MAIN_COURSE)
            .available(true)
            .build();
        restaurant.addDish(dish);
        restaurantRepository.save(restaurant);

        facade.addDishToCart(new AddDishToCartRequest(user.getId(), dish.getId(), 1));

        PlaceOrderRequest orderRequest = new PlaceOrderRequest(
            user.getId(),
            restaurant.getId(),
            PaymentMethod.STUDENT_CREDIT
        );

        PlaceOrderResponse orderResponse = facade.placeOrder(orderRequest);

        assertNotNull(orderResponse);
        assertNotNull(orderResponse.orderId());
        assertNotNull(orderResponse.status());
    }
}

