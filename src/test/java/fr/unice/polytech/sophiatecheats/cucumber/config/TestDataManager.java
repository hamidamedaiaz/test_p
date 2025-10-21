package fr.unice.polytech.sophiatecheats.cucumber.config;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.user.User;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Test data manager for creating consistent test data across scenarios.
 *
 * This class provides factory methods for creating test entities with
 * predictable data that can be reused across different test scenarios.
 */
public class TestDataManager {

    private final Map<String, Dish> dishCatalog = new HashMap<>();
    private final Map<String, User> userCatalog = new HashMap<>();

    public TestDataManager() {
        initializeTestData();
    }

    private void initializeTestData() {
        // Initialize common test dishes
        createDish("Pizza", "Margherita Pizza", "12.50", DishCategory.MAIN_COURSE);
        createDish("Salad", "Fresh Garden Salad", "8.00", DishCategory.STARTER);
        createDish("Burger", "Classic Beef Burger", "15.00", DishCategory.MAIN_COURSE);
        createDish("Fries", "Crispy French Fries", "4.50", DishCategory.STARTER);
        createDish("Pasta", "Spaghetti Carbonara", "13.00", DishCategory.MAIN_COURSE);
        createDish("Soup", "Tomato Soup", "6.00", DishCategory.STARTER);

        // Initialize test users with student credit
        createUserWithCredit("etudiant1@unice.fr", "Jean Dupont", "50.00");
        createUserWithCredit("etudiant2@unice.fr", "Marie Martin", "75.50");
        createUserWithCredit("etudiant3@unice.fr", "Pierre Durand", "25.00");
    }

    public Dish getDish(String name) {
        return dishCatalog.get(name);
    }

    public User getUser(String email) {
        return userCatalog.get(email);
    }

    public Dish createDish(String name, String description, String price, DishCategory category) {
        Dish dish = Dish.builder()
            .name(name)
            .description(description)
            .price(new BigDecimal(price))
            .category(category)
            .available(true)
            .build();
        dishCatalog.put(name, dish);
        return dish;
    }

    public User createUser(String email, String name) {
        User user = new User(email, name);
        userCatalog.put(email, user);
        return user;
    }

    public User createUserWithCredit(String email, String name, String creditAmount) {
        User user = new User(email, name);
        user.addCredit(new BigDecimal(creditAmount));
        userCatalog.put(email, user);
        return user;
    }

    public void clearData() {
        dishCatalog.clear();
        userCatalog.clear();
        initializeTestData();
    }
}
