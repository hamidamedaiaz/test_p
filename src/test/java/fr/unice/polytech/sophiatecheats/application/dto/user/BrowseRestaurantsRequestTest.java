package fr.unice.polytech.sophiatecheats.application.dto.user;

import fr.unice.polytech.sophiatecheats.application.dto.user.request.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.domain.enums.DietType;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.enums.RestaurantType;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BrowseRestaurantsRequestTest {
    BrowseRestaurantsRequest request;

    @Test
    void should_validate_browse_restaurant_request() {
        request = new BrowseRestaurantsRequest(
                DishCategory.MAIN_COURSE,
                true,
                DietType.NONE,
                new BigDecimal("5"),
                new BigDecimal("10"),
                RestaurantType.RESTAURANT);

        assertTrue(request.isValid());
    }

    @Test
    void should_not_validate_browse_restaurant_request() {
        request = new BrowseRestaurantsRequest(
                DishCategory.MAIN_COURSE,
                true,
                DietType.NONE,
                new BigDecimal("15"),
                new BigDecimal("10"),
                RestaurantType.RESTAURANT);

        assertFalse(request.isValid());

        request = new BrowseRestaurantsRequest(
                DishCategory.MAIN_COURSE,
                true,
                DietType.NONE,
                new BigDecimal("-5"),
                new BigDecimal("10"),
                RestaurantType.RESTAURANT);

        assertFalse(request.isValid());

        request = new BrowseRestaurantsRequest(
                DishCategory.MAIN_COURSE,
                true,
                DietType.NONE,
                new BigDecimal("5"),
                new BigDecimal("-10"),
                RestaurantType.RESTAURANT);

        assertFalse(request.isValid());
    }
}
