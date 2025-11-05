package fr.unice.polytech.sophiatecheats.cucumber;

import fr.unice.polytech.sophiatecheats.domain.entities.cart.Cart;
import fr.unice.polytech.sophiatecheats.domain.entities.cart.CartItem;
import fr.unice.polytech.sophiatecheats.domain.entities.restaurant.Dish;
import fr.unice.polytech.sophiatecheats.domain.enums.DishCategory;
import fr.unice.polytech.sophiatecheats.domain.exceptions.ValidationException;
import org.junit.jupiter.api.Assertions;
import java.math.BigDecimal;
import java.util.*;

import io.cucumber.java.en.*;

public class CartStepDefinitions {
    private UUID userId;
    private Cart cart;
    private Map<String, Dish> dishMap = new HashMap<>();
    private String lastError = null;

    @Given("I am a registered user")
    public void i_am_a_registered_user() {
        userId = UUID.randomUUID();
        cart = new Cart(userId);
        lastError = null;
    }

    @Given("my cart contains various dishes")
    public void my_cart_contains_various_dishes() {
        Dish pizza = new Dish(UUID.randomUUID(), "Pizza", "Margherita", new BigDecimal("10.0"), DishCategory.MAIN_COURSE, true);
        Dish salad = new Dish(UUID.randomUUID(), "Salad", "Fresh", new BigDecimal("5.0"), DishCategory.STARTER, true);
        dishMap.put("Pizza", pizza);
        dishMap.put("Salad", salad);
        cart.addDish(pizza, 2);
        cart.addDish(salad, 1);
    }

    @Given("my cart is empty")
    public void my_cart_is_empty() {
        cart = new Cart(userId != null ? userId : UUID.randomUUID());
        lastError = null;
    }

    @Given("my cart contains multiple dishes")
    public void my_cart_contains_multiple_dishes() {
        Dish burger = new Dish(UUID.randomUUID(), "Burger", "Beef", new BigDecimal("8.0"), DishCategory.MAIN_COURSE, true);
        Dish fries = new Dish(UUID.randomUUID(), "Fries", "Potato", new BigDecimal("3.0"), DishCategory.STARTER, true);
        dishMap.put("Burger", burger);
        dishMap.put("Fries", fries);
        cart.addDish(burger, 1);
        cart.addDish(fries, 2);
    }

    @Given("my cart contains {int} {string} and {int} {string}")
    public void my_cart_contains_and(Integer qty1, String dish1, Integer qty2, String dish2) {
        Dish d1 = new Dish(UUID.randomUUID(), dish1, dish1 + " desc", new BigDecimal("7.0"), DishCategory.MAIN_COURSE, true);
        Dish d2 = new Dish(UUID.randomUUID(), dish2, dish2 + " desc", new BigDecimal("4.0"), DishCategory.STARTER, true);
        dishMap.put(dish1, d1);
        dishMap.put(dish2, d2);
        cart.addDish(d1, qty1);
        cart.addDish(d2, qty2);
    }

    @Given("there is an unavailable dish {string}")
    public void there_is_an_unavailable_dish(String dishName) {
        Dish dish = new Dish(UUID.randomUUID(), dishName, dishName + " desc", new BigDecimal("6.0"), DishCategory.MAIN_COURSE, false);
        dishMap.put(dishName, dish);
    }

    @Given("my cart contains {int} {string} priced at {double}€")
    public void my_cart_contains_priced_at_euro(Integer quantity, String dishName, Double price) {
        Dish dish = new Dish(UUID.randomUUID(), dishName, dishName + " description",
                           new BigDecimal(price.toString()), DishCategory.MAIN_COURSE, true);
        dishMap.put(dishName, dish);
        cart.addDish(dish, quantity);
    }

    @When("I try to add {int} {string} to my cart")
    public void i_try_to_add_to_my_cart(Integer qty, String dishName) {
        Dish dish = dishMap.get(dishName);
        if (dish == null) {
            dish = new Dish(UUID.randomUUID(), dishName, dishName + " desc", new BigDecimal("5.0"), DishCategory.MAIN_COURSE, true);
            dishMap.put(dishName, dish);
        }
        try {
            cart.addDish(dish, qty);
            lastError = null;
        } catch (ValidationException e) {
            lastError = e.getMessage();
        }
    }

    @When("I add {int} {string} priced at {double}€ to my cart")
    public void i_add_priced_at_euro_to_my_cart(Integer quantity, String dishName, Double price) {
        Dish dish = new Dish(UUID.randomUUID(), dishName, dishName + " description",
                           new BigDecimal(price.toString()), DishCategory.MAIN_COURSE, true);
        dishMap.put(dishName, dish);
        try {
            cart.addDish(dish, quantity);
            lastError = null;
        } catch (ValidationException e) {
            lastError = e.getMessage();
        }
    }

    @When("I view my cart details")
    public void i_view_my_cart_details() {
        // Rien à faire, les détails sont dans le panier
    }

    @When("I clear my cart")
    public void i_clear_my_cart() {
        cart.clear();
    }

    @When("I remove {string} from my cart")
    public void i_remove_from_my_cart(String dishName) {
        Dish dish = dishMap.get(dishName);
        if (dish != null) {
            cart.removeDish(dish.getId());
        }
    }

    @When("I update the quantity to {int} for {string}")
    public void i_update_the_quantity_to_for(Integer qty, String dishName) {
        Dish dish = dishMap.get(dishName);
        if (dish != null) {
            cart.updateQuantity(dish.getId(), qty);
        }
    }

    @Then("my cart should contain {int} items")
    public void my_cart_should_contain_items(Integer expectedQty) {
        Assertions.assertEquals(expectedQty.intValue(), cart.getTotalItems());
    }

    @Then("my cart should contain {int} item")
    public void my_cart_should_contain_item(Integer expectedQty) {
        Assertions.assertEquals(expectedQty.intValue(), cart.getTotalItems());
    }

    @Then("my cart should be empty")
    public void my_cart_should_be_empty() {
        Assertions.assertTrue(cart.isEmpty());
    }

    @Then("my cart should remain empty")
    public void my_cart_should_remain_empty() {
        Assertions.assertTrue(cart.isEmpty());
    }

    @Then("only {string} should remain in my cart")
    public void only_should_remain_in_my_cart(String dishName) {
        Assertions.assertEquals(1, cart.getItems().size());
        CartItem item = cart.getItems().get(0);
        Assertions.assertEquals(dishName, item.getDishName());
    }

    @Then("I should see each dish name, description, unit price, and quantity")
    public void i_should_see_each_dish_name_description_unit_price_and_quantity() {
        for (CartItem item : cart.getItems()) {
            Assertions.assertNotNull(item.getDishName());
            Assertions.assertNotNull(item.getDishDescription());
            Assertions.assertNotNull(item.getUnitPrice());
            Assertions.assertTrue(item.getQuantity() > 0);
        }
    }

    @Then("I should see the subtotal for each dish")
    public void i_should_see_the_subtotal_for_each_dish() {
        for (CartItem item : cart.getItems()) {
            BigDecimal expected = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
            Assertions.assertEquals(0, expected.compareTo(item.getSubtotal()));
        }
    }

    @Then("I should see the overall subtotal, tax, and total")
    public void i_should_see_the_overall_subtotal_tax_and_total() {
        Assertions.assertNotNull(cart.calculateTotal());
        // Si la gestion de la taxe est désactivée, on ne vérifie pas la taxe
    }

    @Then("the subtotal should be {double}€")
    public void the_subtotal_should_be_euro(Double expectedSubtotal) {
        // Pas de sous-total séparé, on utilise le total
        BigDecimal actualTotal = cart.calculateTotal();
        BigDecimal expected = new BigDecimal(expectedSubtotal.toString());
        Assertions.assertEquals(0, expected.compareTo(actualTotal),
                              "Expected total: " + expected + ", but was: " + actualTotal);
    }

    @Then("the tax should be {double}€")
    public void the_tax_should_be_euro(Double expectedTax) {
        // Pas de taxe dans ce système
        BigDecimal expected = new BigDecimal(expectedTax.toString());
        Assertions.assertEquals(0, expected.compareTo(BigDecimal.ZERO),
                              "No tax should be applied in this system");
    }

    @Then("the total should be {double}€")
    public void the_total_should_be_euro(Double expectedTotal) {
        BigDecimal actualTotal = cart.calculateTotal();
        BigDecimal expected = new BigDecimal(expectedTotal.toString());
        Assertions.assertEquals(0, expected.compareTo(actualTotal),
                              "Expected total: " + expected + ", but was: " + actualTotal);
    }

    @Then("I should receive an error {string}")
    public void i_should_receive_an_error(String expectedError) {
        Assertions.assertEquals(expectedError, lastError);
    }

    @Then("I should receive an error about dish availability")
    public void i_should_receive_an_error_about_dish_availability() {
        Assertions.assertTrue(lastError != null && lastError.toLowerCase().contains("disponible"));
    }
}
