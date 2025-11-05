package fr.unice.polytech.sophiatecheats.application.dto.restaurant;

import fr.unice.polytech.sophiatecheats.application.dto.DTO;

import java.util.List;

/**
 * Response DTO for browsing restaurants containing the list of restaurants and their dishes.
 */
public record BrowseRestaurantsResponse(
        List<RestaurantDto> restaurants
) implements DTO {

    @Override
    public boolean isValid() {
        return restaurants != null;
    }
}