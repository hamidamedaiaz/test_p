package fr.unice.polytech.sophiatecheats.application.usecases.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.*;
import fr.unice.polytech.sophiatecheats.application.usecases.UseCase;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;

import java.util.List;

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
        if (request.cuisineType() != null && request.availabilityFilter() != null) {
            if (request.availabilityFilter()) {
                return restaurantRepository.findOpenByDishCategory(request.cuisineType());
            } else {
                return restaurantRepository.findByDishCategory(request.cuisineType()).stream()
                        .filter(restaurant -> !restaurant.isOpen())
                        .toList();
            }
        } else if (request.cuisineType() != null) {
            return restaurantRepository.findByDishCategory(request.cuisineType());
        } else if (request.availabilityFilter() != null) {
            return restaurantRepository.findByAvailability(request.availabilityFilter());
        } else {
            return restaurantRepository.findAll();
        }
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
                dish.isAvailable()
        );
    }
}
