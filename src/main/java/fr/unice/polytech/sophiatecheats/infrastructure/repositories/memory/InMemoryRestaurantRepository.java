package fr.unice.polytech.sophiatecheats.infrastructure.repositories.memory;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

/**
 * In-memory implementation of RestaurantRepository for MVP.
 * Contains sample data for testing and development.
 */
public class InMemoryRestaurantRepository extends InMemoryRepository<Restaurant, UUID> implements RestaurantRepository {

    public InMemoryRestaurantRepository() {
        initializeWithSampleData();
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
        cafeteria.setOpeningHours(LocalTime.of(8, 0), LocalTime.of(18, 0));
        
        cafeteria.addDish(new Dish(UUID.randomUUID(), "Salade César", "Salade fraîche avec croûtons et parmesan",
                new BigDecimal("8.50"), DishCategory.MAIN_COURSE, true));
        cafeteria.addDish(new Dish(UUID.randomUUID(), "Sandwich Jambon", "Pain de mie, jambon, beurre, salade",
                new BigDecimal("4.20"), DishCategory.MAIN_COURSE, true));
        cafeteria.addDish(new Dish(UUID.randomUUID(), "Café", "Café expresso",
                new BigDecimal("1.50"), DishCategory.BEVERAGE, true));
        cafeteria.addDish(new Dish(UUID.randomUUID(), "Tarte aux pommes", "Tarte maison aux pommes",
                new BigDecimal("3.80"), DishCategory.DESSERT, true));

        Restaurant foodTruck = new Restaurant("Food Truck Bio", "Parking Sud Campus");
        foodTruck.setOpeningHours(LocalTime.of(11, 30), LocalTime.of(14, 30));
        
        foodTruck.addDish(new Dish(UUID.randomUUID(), "Bowl Végétarien", "Quinoa, légumes grillés, avocat",
                new BigDecimal("9.90"), DishCategory.VEGETARIAN, true));
        foodTruck.addDish(new Dish(UUID.randomUUID(), "Burger Bio", "Steak de bœuf bio, fromage, légumes",
                new BigDecimal("12.50"), DishCategory.MAIN_COURSE, true));
        foodTruck.addDish(new Dish(UUID.randomUUID(), "Jus de fruits frais", "Orange, pomme ou carotte",
                new BigDecimal("3.00"), DishCategory.BEVERAGE, true));
        foodTruck.addDish(new Dish(UUID.randomUUID(), "Brownie Vegan", "Brownie sans produits animaux",
                new BigDecimal("4.50"), DishCategory.DESSERT, true));

        Restaurant pizzeria = new Restaurant("Pizzeria du Campus", "Bâtiment C - Rez-de-chaussée");
        pizzeria.setOpeningHours(LocalTime.of(12, 0), LocalTime.of(22, 0));
        pizzeria.close();
        
        pizzeria.addDish(new Dish(UUID.randomUUID(), "Pizza Margherita", "Tomate, mozzarella, basilic",
                new BigDecimal("11.00"), DishCategory.MAIN_COURSE, true));
        pizzeria.addDish(new Dish(UUID.randomUUID(), "Tiramisu", "Dessert italien traditionnel",
                new BigDecimal("5.50"), DishCategory.DESSERT, true));
        pizzeria.addDish(new Dish(UUID.randomUUID(), "Coca-Cola", "Boisson gazeuse",
                new BigDecimal("2.50"), DishCategory.BEVERAGE, true));

        save(cafeteria);
        save(foodTruck);
        save(pizzeria);
    }
}