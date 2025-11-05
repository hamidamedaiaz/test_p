package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;

/**
 * Request DTO for browsing restaurants with filtering criteria.
 */
public record BrowseRestaurantsRequest(
        DishCategory cuisineType,
        Boolean availabilityFilter
) implements DTO {

    @Override
    public boolean isValid() {
        return true;
    }
}