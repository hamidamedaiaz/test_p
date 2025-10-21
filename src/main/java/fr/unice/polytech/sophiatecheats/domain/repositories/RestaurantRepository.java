package fr.unice.polytech.sophiatecheats.domain.repositories;

import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

import java.util.List;
import java.util.UUID;

/**
 * Repository interface for Restaurant entities.
 * Defines domain-specific query methods for restaurant browsing functionality.
 */
public interface RestaurantRepository extends Repository<Restaurant, UUID> {

    /**
     * Find restaurants by availability status.
     * @param isOpen true to find open restaurants, false for closed ones
     * @return list of restaurants matching the availability criteria
     */
    List<Restaurant> findByAvailability(boolean isOpen);

    /**
     * Find restaurants that serve dishes of the specified category.
     * @param category the dish category to search for
     * @return list of restaurants that have dishes in the specified category
     */
    List<Restaurant> findByDishCategory(DishCategory category);

    /**
     * Find all open restaurants that serve dishes of the specified category.
     * @param category the dish category to search for
     * @return list of open restaurants with dishes in the specified category
     */
    List<Restaurant> findOpenByDishCategory(DishCategory category);
}