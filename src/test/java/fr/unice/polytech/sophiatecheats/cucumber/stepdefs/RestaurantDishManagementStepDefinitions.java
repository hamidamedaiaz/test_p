package fr.unice.polytech.sophiatecheats.cucumber.stepdefs;

import fr.unice.polytech.sophiatecheats.application.dto.restaurant.management.*;
import fr.unice.polytech.sophiatecheats.application.usecases.restaurant.*;
import fr.unice.polytech.sophiatecheats.cucumber.hooks.TestHooks;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Restaurant;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.repositories.RestaurantRepository;
import fr.unice.polytech.sophiatecheats.infrastructure.config.ApplicationConfig;
import io.cucumber.java.en.*;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Step definitions for restaurant dish management BDD tests in English.
 * Follows Clean Architecture and uses business use cases.
 */
public class RestaurantDishManagementStepDefinitions {

    private Restaurant restaurant;
    private UUID restaurantId;
    private UUID lastDishId;

    private AddDishToRestaurantResponse addResponse;
    private UpdateDishResponse updateResponse;
    private RemoveDishFromRestaurantResponse removeResponse;
    private boolean operationFailed;
    private String errorMessage;

    private RestaurantRepository getRestaurantRepository() {
        ApplicationConfig config = TestHooks.getApplicationConfig();
        return config.getInstance(RestaurantRepository.class);
    }

    private AddDishToRestaurantUseCase getAddDishUseCase() {
        return new AddDishToRestaurantUseCase(getRestaurantRepository());
    }

    private UpdateDishUseCase getUpdateDishUseCase() {
        return new UpdateDishUseCase(getRestaurantRepository());
    }

    private RemoveDishFromRestaurantUseCase getRemoveDishUseCase() {
        return new RemoveDishFromRestaurantUseCase(getRestaurantRepository());
    }

    private void refreshRestaurant() {
        if (restaurantId != null) {
            restaurant = getRestaurantRepository().findById(restaurantId).orElse(restaurant);
        }
    }

    // ===== CONTEXTE =====
    @Given("a restaurant {string} located at {string}")
    public void aRestaurantLocatedAt(String name, String address) {
        restaurant = new Restaurant(name, address);
        restaurantId = restaurant.getId();
        getRestaurantRepository().save(restaurant);
        operationFailed = false;
        errorMessage = "";
        addResponse = null;
        updateResponse = null;
        removeResponse = null;
    }

    @Given("the restaurant menu is empty")
    public void theRestaurantMenuIsEmpty() { assertTrue(restaurant.getMenu().isEmpty()); }

    @Given("the menu already contains a dish {string}")
    public void theMenuAlreadyContainsADish(String dishName) {
        AddDishToRestaurantRequest req = new AddDishToRestaurantRequest(restaurantId, dishName, "Existing", new BigDecimal("10.00"), DishCategory.MAIN_COURSE, true);
        AddDishToRestaurantResponse r = getAddDishUseCase().execute(req);
        assertTrue(r.success());
        lastDishId = r.dishId();
        refreshRestaurant();
    }

    @Given("the menu contains a dish {string} priced at {double} euros")
    public void theMenuContainsDishPriced(String name, double price) {
        AddDishToRestaurantResponse r = getAddDishUseCase().execute(new AddDishToRestaurantRequest(restaurantId, name, "Test", BigDecimal.valueOf(price), DishCategory.MAIN_COURSE, true));
        assertTrue(r.success());
        lastDishId = r.dishId();
        refreshRestaurant();
    }

    @Given("the menu contains an available dish {string}")
    public void theMenuContainsAvailableDish(String name) { theMenuAlreadyContainsADish(name); }

    @Given("the menu contains an unavailable dish {string}")
    public void theMenuContainsUnavailableDish(String name) {
        theMenuAlreadyContainsADish(name);
        getUpdateDishUseCase().execute(new UpdateDishRequest(restaurantId, lastDishId, null, null, null, null, false));
        refreshRestaurant();
    }

    @Given("the menu contains a dish {string}")
    public void theMenuContainsDish(String name) { theMenuAlreadyContainsADish(name); }

    // ===== ACTIONS =====
    @When("the administrator adds a dish {string} with description {string} priced at {double} euros in category {string}")
    public void adminAddsDish(String name, String description, double price, String cat) {
        AddDishToRestaurantRequest req = new AddDishToRestaurantRequest(restaurantId, name, description, BigDecimal.valueOf(price), DishCategory.valueOf(cat), true);
        addResponse = getAddDishUseCase().execute(req);
        if (addResponse.success()) { lastDishId = addResponse.dishId(); refreshRestaurant(); } else { operationFailed = true; errorMessage = addResponse.message(); }
    }

    @When("the administrator tries to add a dish {string} with description {string} priced at {double} euros")
    public void adminTriesAdd(String name, String description, double price) {
        try {
            addResponse = getAddDishUseCase().execute(new AddDishToRestaurantRequest(restaurantId, name, description, BigDecimal.valueOf(price), DishCategory.MAIN_COURSE, true));
            operationFailed = !addResponse.success();
            errorMessage = addResponse.message();
            if (addResponse.success()) refreshRestaurant();
        } catch (IllegalArgumentException e) {
            operationFailed = true; errorMessage = e.getMessage();
            addResponse = new AddDishToRestaurantResponse(null, e.getMessage(), false);
        }
    }

    @When("the administrator modifies the dish name to {string}")
    public void adminModifiesName(String newName) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, newName, null, null, null, null)); }

    @When("the administrator modifies the dish description to {string}")
    public void adminModifiesDescription(String newDesc) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, null, newDesc, null, null, null)); }

    @When("the administrator modifies the dish price to {string}")
    public void adminModifiesPrice(String newPrice) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, null, null, new BigDecimal(newPrice), null, null)); }

    @When("the administrator modifies the dish category to {string}")
    public void adminModifiesCategory(String newCat) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, null, null, null, DishCategory.valueOf(newCat), null)); }

    @When("the administrator makes the dish {string} unavailable")
    public void adminMakesUnavailable(String ignored) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, null, null, null, null, false)); }

    @When("the administrator makes the dish {string} available")
    public void adminMakesAvailable(String ignored) { updateAndStore(new UpdateDishRequest(restaurantId, lastDishId, null, null, null, null, true)); }

    @When("the administrator removes the dish {string}")
    public void adminRemovesDish(String ignored) {
        removeResponse = getRemoveDishUseCase().execute(new RemoveDishFromRestaurantRequest(restaurantId, lastDishId));
        operationFailed = !removeResponse.success();
        errorMessage = removeResponse.message();
        if (removeResponse.success()) refreshRestaurant();
    }

    @When("the administrator tries to remove the dish {string}")
    public void adminTriesRemove(String name) {
        UUID randomId = UUID.randomUUID();
        removeResponse = getRemoveDishUseCase().execute(new RemoveDishFromRestaurantRequest(restaurantId, randomId));
        operationFailed = !removeResponse.success();
        errorMessage = removeResponse.message();
    }

    @When("the administrator tries to update the price of dish {string} to {double} euros")
    public void adminTriesUpdatePrice(String name, double price) {
        UUID randomId = UUID.randomUUID();
        updateResponse = getUpdateDishUseCase().execute(new UpdateDishRequest(restaurantId, randomId, null, null, BigDecimal.valueOf(price), null, null));
        operationFailed = !updateResponse.success();
        errorMessage = updateResponse.message();
    }

    private void updateAndStore(UpdateDishRequest req) {
        updateResponse = getUpdateDishUseCase().execute(req);
        operationFailed = !updateResponse.success();
        errorMessage = updateResponse.message();
        if (updateResponse.success()) refreshRestaurant();
    }

    // ===== ASSERTIONS ===== (gardées simples)
    @Then("the dish {string} should be present in the menu")
    public void dishPresent(String name) { refreshRestaurant(); assertTrue(restaurant.findDishByName(name).isPresent()); }

    @Then("the dish should be available for ordering")
    public void dishAvailable() { refreshRestaurant(); assertTrue(restaurant.findDishById(lastDishId).get().isAvailable()); }

    @Then("the menu should contain {int} dish(es)")
    public void menuShouldContain(int count) { refreshRestaurant(); assertEquals(count, restaurant.getMenu().size()); }

    @Then("the menu should contain {int} dish") public void menuDish(int c){ menuShouldContain(c);}
    @Then("the menu should still contain {int} dish") public void menuStill(int c){ menuShouldContain(c);}

    @Then("the dish addition should be rejected")
    public void additionRejected() { assertTrue(operationFailed); assertFalse(addResponse.success()); }

    @Then("the error message should contain {string}")
    public void errorContains(String expected) { assertTrue(errorMessage.toLowerCase().contains(expected.toLowerCase())); }

    @Then("the dish should have name {string}")
    public void dishHasName(String n) { refreshRestaurant(); assertEquals(n, restaurant.findDishById(lastDishId).get().getName()); }

    @Then("the dish should have description {string}")
    public void dishHasDesc(String d) { refreshRestaurant(); assertEquals(d, restaurant.findDishById(lastDishId).get().getDescription()); }

    @Then("the dish should have price {string}")
    public void dishHasPrice(String p) { refreshRestaurant(); assertEquals(new BigDecimal(p), restaurant.findDishById(lastDishId).get().getPrice()); }

    @Then("the dish should have category {string}")
    public void dishHasCat(String c) { refreshRestaurant(); assertEquals(DishCategory.valueOf(c), restaurant.findDishById(lastDishId).get().getCategory()); }

    @Then("the modification should be confirmed")
    public void modificationConfirmed() { assertFalse(operationFailed); assertTrue(updateResponse.success()); }

    @Then("the dish {string} should be marked as unavailable")
    public void dishUnavailable(String n) { refreshRestaurant(); assertFalse(restaurant.findDishById(lastDishId).get().isAvailable()); }

    @Then("the dish should remain in the menu")
    public void dishRemain() { refreshRestaurant(); assertTrue(restaurant.findDishById(lastDishId).isPresent()); }

    @Then("the dish {string} should be marked as available")
    public void dishAvailableAgain(String n) { refreshRestaurant(); assertTrue(restaurant.findDishById(lastDishId).get().isAvailable()); }

    @Then("the dish {string} should no longer be present in the menu")
    public void dishRemoved(String n) { refreshRestaurant(); assertTrue(restaurant.findDishByName(n).isEmpty()); }

    @Then("the menu should be empty")
    public void menuEmpty() { assertTrue(restaurant.getMenu().isEmpty()); }

    @Then("the removal should be confirmed")
    public void removalConfirmed() { assertFalse(operationFailed); assertTrue(removeResponse.success()); }

    @Then("the removal should be rejected")
    public void removalRejected() { assertTrue(operationFailed); assertFalse(removeResponse.success()); }

    @Then("the modification should be rejected")
    public void modificationRejected() { assertTrue(operationFailed); assertFalse(updateResponse.success()); }
}
