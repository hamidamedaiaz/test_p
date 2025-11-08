package fr.unice.polytech.sophiatecheats.application.usecases.user;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.*;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.dishManagement.DishDto;
import fr.unice.polytech.sophiatecheats.application.dto.user.request.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.user.response.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

/**
 * Use case for browsing restaurants and their available dishes in the SophiaTech campus ecosystem.
 * 
 * <p>This use case implements the business logic for retrieving restaurant information
 * with optional filtering capabilities. It follows the Clean Architecture principles
 * by depending only on domain abstractions.</p>
 * 
 * <h3>Supported Filters:</h3>
 * <ul>
 *     <li>Cuisine type filtering by dish category</li>
 *     <li>Availability status (open/closed restaurants)</li>
 *     <li>Diet type (vegetarian, vegan, gluten-free, etc.)</li>
 *     <li>Price range filtering (min and max price)</li>
 *     <li>Restaurant type (CROUS, restaurant, food truck, etc.)</li>
 *     <li>Combined filtering for precise queries</li>
 * </ul>
 * 
 * @author SophiaTech Eats Backend Team
 * @since 1.0
 */
public class BrowseRestaurantsUseCase implements UseCase<BrowseRestaurantsRequest, BrowseRestaurantsResponse> {

    private final RestaurantRepository restaurantRepository;

    public BrowseRestaurantsUseCase(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @Override
    public BrowseRestaurantsResponse execute(BrowseRestaurantsRequest request) {
        if (request == null || !request.isValid()) {
            return new BrowseRestaurantsResponse(List.of());
        }

        List<Restaurant> restaurants = findRestaurants(request);
        
        List<RestaurantDto> restaurantDtos = restaurants.stream()
                .map(this::mapToDto)
                .toList();

        return new BrowseRestaurantsResponse(restaurantDtos);
    }

    private List<Restaurant> findRestaurants(BrowseRestaurantsRequest request) {
        // Start with all restaurants
        Stream<Restaurant> restaurantStream = restaurantRepository.findAll().stream();

        // Apply availability filter
        if (request.availabilityFilter() != null) {
            if (request.availabilityFilter()) {
                restaurantStream = restaurantStream.filter(Restaurant::isOpen);
            } else {
                restaurantStream = restaurantStream.filter(restaurant -> !restaurant.isOpen());
            }
        }

        // Apply cuisine type filter
        if (request.cuisineType() != null) {
            restaurantStream = restaurantStream.filter(restaurant ->
                restaurant.getCuisineType() != null && restaurant.getCuisineType().equals(request.cuisineType()) ||
                restaurant.getMenu().stream().anyMatch(dish -> dish.getCategory().equals(request.cuisineType()))
            );
        }

        // Apply restaurant type filter
        if (request.restaurantType() != null) {
            restaurantStream = restaurantStream.filter(restaurant ->
                restaurant.getRestaurantType() != null && restaurant.getRestaurantType().equals(request.restaurantType())
            );
        }

        // Apply diet type filter
        if (request.dietType() != null) {
            restaurantStream = restaurantStream.filter(restaurant ->
                restaurant.getMenu().stream().anyMatch(dish -> dish.hasDietType(request.dietType()))
            );
        }

        // Apply price range filter
        if (request.minPrice() != null || request.maxPrice() != null) {
            restaurantStream = restaurantStream.filter(restaurant ->
                hasMenuItemsInPriceRange(restaurant, request.minPrice(), request.maxPrice())
            );
        }

        return restaurantStream.toList();
    }

    /**
     * Checks if a restaurant has at least one dish within the specified price range.
     */
    private boolean hasMenuItemsInPriceRange(Restaurant restaurant, BigDecimal minPrice, BigDecimal maxPrice) {
        return restaurant.getMenu().stream().anyMatch(dish -> {
            BigDecimal dishPrice = dish.getPrice();
            boolean meetsMin = minPrice == null || dishPrice.compareTo(minPrice) >= 0;
            boolean meetsMax = maxPrice == null || dishPrice.compareTo(maxPrice) <= 0;
            return meetsMin && meetsMax;
        });
    }

    private RestaurantDto mapToDto(Restaurant restaurant) {
        List<DishDto> dishDtos = restaurant.getAvailableDishes().stream()
                .map(this::mapDishToDto)
                .toList();

        return new RestaurantDto(
                restaurant.getId(),
                restaurant.getName(),
                restaurant.getAddress(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.isOpen(),
                dishDtos
        );
    }

    private DishDto mapDishToDto(Dish dish) {
        return new DishDto(
                dish.getId(),
                dish.getName(),
                dish.getDescription(),
                dish.getPrice(),
                dish.getCategory(),
                dish.isAvailable(),
                dish.getDietTypes()
        );
    }
}
