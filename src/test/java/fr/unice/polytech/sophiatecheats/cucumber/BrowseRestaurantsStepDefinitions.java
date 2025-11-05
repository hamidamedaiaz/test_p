package fr.unice.polytech.sophiatecheats.cucumber;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsRequest;
import fr.unice.polytech.sophiatecheats.application.dto.restaurant.BrowseRestaurantsResponse;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.BrowseRestaurantsUseCase;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

public class BrowseRestaurantsStepDefinitions {

    private ApplicationConfig config;
    private BrowseRestaurantsUseCase useCase;
    private BrowseRestaurantsResponse response;

    @Given("the system has sample restaurants with dishes")
    public void theSystemHasSampleRestaurantsWithDishes() {
        config = new ApplicationConfig();
        useCase = config.getInstance(BrowseRestaurantsUseCase.class);
    }

    @When("I browse restaurants without any filters")
    public void iBrowseRestaurantsWithoutAnyFilters() {
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, null);
        response = useCase.execute(request);
    }

    @When("I browse restaurants filtering by {string} status")
    public void iBrowseRestaurantsFilteringByStatus(String status) {
        boolean isOpen = "open".equalsIgnoreCase(status);
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(null, isOpen);
        response = useCase.execute(request);
    }

    @When("I browse restaurants filtering by {string} cuisine")
    public void iBrowseRestaurantsFilteringByCuisine(String cuisine) {
        DishCategory category = DishCategory.valueOf(cuisine);
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(category, null);
        response = useCase.execute(request);
    }

    @When("I browse restaurants filtering by {string} status and {string} cuisine")
    public void iBrowseRestaurantsFilteringByStatusAndCuisine(String status, String cuisine) {
        boolean isOpen = "open".equalsIgnoreCase(status);
        DishCategory category = DishCategory.valueOf(cuisine);
        BrowseRestaurantsRequest request = new BrowseRestaurantsRequest(category, isOpen);
        response = useCase.execute(request);
    }

    @Then("I should see all available restaurants")
    public void iShouldSeeAllAvailableRestaurants() {
        assertNotNull(response);
        assertNotNull(response.restaurants());
        assertEquals(3, response.restaurants().size()); // Sample data has 3 restaurants
    }

    @Then("each restaurant should display its basic information")
    public void eachRestaurantShouldDisplayItsBasicInformation() {
        response.restaurants().forEach(restaurant -> {
            assertNotNull(restaurant.id());
            assertNotNull(restaurant.name());
            assertFalse(restaurant.name().trim().isEmpty());
            assertNotNull(restaurant.address());
            assertFalse(restaurant.address().trim().isEmpty());
        });
    }

    @Then("each restaurant should display its available dishes")
    public void eachRestaurantShouldDisplayItsAvailableDishes() {
        response.restaurants().forEach(restaurant -> {
            assertNotNull(restaurant.dishes());
            // At least some restaurants should have dishes
        });
    }

    @Then("I should see only open restaurants")
    public void iShouldSeeOnlyOpenRestaurants() {
        assertNotNull(response);
        assertFalse(response.restaurants().isEmpty());
        // Should be 2 open restaurants in sample data (pizzeria is closed)
        assertEquals(2, response.restaurants().size());
    }

    @Then("all returned restaurants should be open")
    public void allReturnedRestaurantsShouldBeOpen() {
        response.restaurants().forEach(restaurant -> 
            assertTrue(restaurant.isOpen(), 
                "Restaurant " + restaurant.name() + " should be open"));
    }

    @Then("I should see only restaurants that serve vegetarian dishes")
    public void iShouldSeeOnlyRestaurantsThatServeVegetarianDishes() {
        assertNotNull(response);
        assertFalse(response.restaurants().isEmpty());
        // Only Food Truck Bio has vegetarian dishes in sample data
        assertEquals(1, response.restaurants().size());
        assertEquals("Food Truck Bio", response.restaurants().get(0).name());
    }

    @Then("each returned restaurant should have at least one vegetarian dish")
    public void eachReturnedRestaurantShouldHaveAtLeastOneVegetarianDish() {
        response.restaurants().forEach(restaurant -> {
            boolean hasVegetarianDish = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.VEGETARIAN);
            assertTrue(hasVegetarianDish, 
                "Restaurant " + restaurant.name() + " should have at least one vegetarian dish");
        });
    }

    @Then("I should see only open restaurants that serve main courses")
    public void iShouldSeeOnlyOpenRestaurantsThatServeMainCourses() {
        assertNotNull(response);
        assertFalse(response.restaurants().isEmpty());
        // Should have open restaurants with main courses
        assertTrue(response.restaurants().size() >= 1);
    }

    @Then("each returned restaurant should have at least one main course dish")
    public void eachReturnedRestaurantShouldHaveAtLeastOneMainCourseDish() {
        response.restaurants().forEach(restaurant -> {
            boolean hasMainCourse = restaurant.dishes().stream()
                .anyMatch(dish -> dish.category() == DishCategory.MAIN_COURSE);
            assertTrue(hasMainCourse, 
                "Restaurant " + restaurant.name() + " should have at least one main course dish");
        });
    }

    @Then("I should see an empty list of restaurants")
    public void iShouldSeeAnEmptyListOfRestaurants() {
        assertNotNull(response);
        assertTrue(response.restaurants().isEmpty());
    }

    @Then("each dish should display its name, description, price and category")
    public void eachDishShouldDisplayItsNameDescriptionPriceAndCategory() {
        response.restaurants().forEach(restaurant -> {
            restaurant.dishes().forEach(dish -> {
                assertNotNull(dish.id());
                assertNotNull(dish.name());
                assertFalse(dish.name().trim().isEmpty());
                assertNotNull(dish.description()); // Description can be empty but not null
                assertNotNull(dish.price());
                assertTrue(dish.price().compareTo(BigDecimal.ZERO) >= 0);
                assertNotNull(dish.category());
            });
        });
    }

    @Then("only available dishes should be displayed")
    public void onlyAvailableDishesShouldBeDisplayed() {
        response.restaurants().forEach(restaurant -> {
            restaurant.dishes().forEach(dish -> 
                assertTrue(dish.available(), 
                    "Dish " + dish.name() + " should be available"));
        });
    }
}