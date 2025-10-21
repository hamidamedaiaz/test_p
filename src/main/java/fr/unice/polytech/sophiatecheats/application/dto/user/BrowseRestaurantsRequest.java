package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;

import java.math.BigDecimal;

/**
 * Request DTO for browsing restaurants with filtering criteria.
 */
public record BrowseRestaurantsRequest(
        DishCategory cuisineType,
        Boolean availabilityFilter,
        DietType dietType,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        RestaurantType restaurantType
) implements DTO {

    @Override
    public boolean isValid() {
        // Validate that minPrice is not greater than maxPrice if both are provided
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            return false;
        }
        // Validate that prices are not negative
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            return false;
        }
        return true;
    }
}