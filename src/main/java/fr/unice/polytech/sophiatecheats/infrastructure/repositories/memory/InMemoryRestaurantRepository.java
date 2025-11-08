package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.DuplicateRestaurantException;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.*;

/**
 * In-memory implementation of RestaurantRepository for MVP.
 * Contains sample data for testing and development.
 */
public class InMemoryRestaurantRepository extends InMemoryRepository<Restaurant, UUID> implements RestaurantRepository {
    private final Map<UUID, Restaurant> restaurants = new HashMap<>();

    public InMemoryRestaurantRepository() {
        this(true);
    }

    public InMemoryRestaurantRepository(boolean withSampleData) {
        if (withSampleData) {
            initializeWithSampleData();
        }
    }

    @Override
    protected UUID extractId(Restaurant entity) {
        return entity.getId();
    }

    @Override
    public List<Restaurant> findByAvailability(boolean isOpen) {
        return findAll().stream()
                .filter(restaurant -> restaurant.isOpen() == isOpen)
                .toList();
    }

    @Override
    public List<Restaurant> findByDishCategory(DishCategory category) {
        return findAll().stream()
                .filter(restaurant -> hasMenuCategory(restaurant, category))
                .toList();
    }

    @Override
    public List<Restaurant> findOpenByDishCategory(DishCategory category) {
        return findAll().stream()
                .filter(Restaurant::isOpen)
                .filter(restaurant -> hasMenuCategory(restaurant, category))
                .toList();
    }

    private boolean hasMenuCategory(Restaurant restaurant, DishCategory category) {
        return restaurant.getMenu().stream()
                .anyMatch(dish -> dish.getCategory() == category);
    }

    private void initializeWithSampleData() {
        Restaurant cafeteria = new Restaurant("La Cafétéria", "Campus Sophia Antipolis - Bâtiment A");
        cafeteria.setSchedule(LocalTime.of(8, 0), LocalTime.of(18, 0));

        cafeteria.addDish(Dish.builder()
                .name("Salade César")
                .description("Salade fraîche avec croûtons et parmesan")
                .price(new BigDecimal("8.50"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Sandwich Jambon")
                .description("Pain de mie, jambon, beurre, salade")
                .price(new BigDecimal("4.20"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Café")
                .description("Café expresso")
                .price(new BigDecimal("1.50"))
                .category(DishCategory.BEVERAGE)
                .available(true)
                .build());
        cafeteria.addDish(Dish.builder()
                .name("Tarte aux pommes")
                .description("Tarte maison aux pommes")
                .price(new BigDecimal("3.80"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());

        Restaurant foodTruck = new Restaurant("Food Truck Bio", "Parking Sud Campus");
        foodTruck.setSchedule(LocalTime.of(11, 30), LocalTime.of(14, 30));

        foodTruck.addDish(Dish.builder()
                .name("Bowl Végétarien")
                .description("Quinoa, légumes grillés, avocat")
                .price(new BigDecimal("9.90"))
                .category(DishCategory.VEGETARIAN)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Burger Bio")
                .description("Steak de bœuf bio, fromage, légumes")
                .price(new BigDecimal("12.50"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Jus de fruits frais")
                .description("Orange, pomme ou carotte")
                .price(new BigDecimal("3.00"))
                .category(DishCategory.BEVERAGE)
                .available(true)
                .build());
        foodTruck.addDish(Dish.builder()
                .name("Brownie Vegan")
                .description("Brownie sans produits animaux")
                .price(new BigDecimal("4.50"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());

        Restaurant pizzeria = new Restaurant("Pizzeria du Campus", "Bâtiment C - Rez-de-chaussée");
        pizzeria.setSchedule(LocalTime.of(12, 0), LocalTime.of(22, 0));
        pizzeria.close();
        
        pizzeria.addDish(Dish.builder()
                .name("Pizza Margherita")
                .description("Tomate, mozzarella, basilic")
                .price(new BigDecimal("11.00"))
                .category(DishCategory.MAIN_COURSE)
                .available(true)
                .build());
        pizzeria.addDish(Dish.builder()
                .name("Tiramisu")
                .description("Dessert italien traditionnel")
                .price(new BigDecimal("5.50"))
                .category(DishCategory.DESSERT)
                .available(true)
                .build());
        pizzeria.addDish(Dish.builder()
                .name("Coca-Cola")
                .description("Boisson gazeuse")
                .price(new BigDecimal("2.50"))
                .category(DishCategory.BEVERAGE)
                .available(true)
                .build());

        save(cafeteria);
        save(foodTruck);
        save(pizzeria);
    }

    @Override
    public Restaurant save(Restaurant restaurant) {
        isDuplicate(restaurant, restaurants);
        storage.put(restaurant.getId(), restaurant);
        return restaurant;
    }

    public static void isDuplicate(Restaurant restaurant, Map<UUID, Restaurant> restaurants) {
        boolean duplicate = restaurants.values().stream().anyMatch(r ->
                !r.getId().equals(restaurant.getId()) && r.getName().equalsIgnoreCase(restaurant.getName())
                        && r.getAddress().equalsIgnoreCase(restaurant.getAddress())
        );

        if (duplicate) {
            throw new DuplicateRestaurantException(restaurant.getName(), restaurant.getAddress());
        }

        restaurants.put(restaurant.getId(), restaurant);
    }

    @Override
    public Optional<Restaurant> findById(UUID uuid) {
        return Optional.ofNullable(restaurants.get(uuid));
    }

    @Override
    public List<Restaurant> findAll() {
        return new ArrayList<>(restaurants.values());
    }

    @Override
    public boolean deleteById(UUID uuid) {
        return false;
    }

    @Override
    public boolean existsById(UUID uuid) {
        return false;
    }

    public void delete(Restaurant restaurant) {
        restaurants.remove(restaurant.getId());
    }

    public Restaurant findByName(String name) {
        return restaurants.values().stream()
                .filter(r -> r.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}