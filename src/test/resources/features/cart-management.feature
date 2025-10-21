Feature: Add dish to cart and see total

  As a campus user
  I want to add a dish to my cart and see the total price
  So that I can build my order and know how much I'll pay

  Background:
    Given the system has sample restaurants with dishes
    And I am a registered user

  @smoke
  Scenario: Remove dish from cart
    Given my cart contains 2 "Pizza Margherita" and 1 "Caesar Salad"
    When I remove "Pizza Margherita" from my cart
    Then my cart should contain 1 item
    And only "Caesar Salad" should remain in my cart

  @smoke
  Scenario: Clear entire cart
    Given my cart contains multiple dishes
    When I clear my cart
    Then my cart should be empty

  Scenario: Handle maximum quantity per dish
    Given my cart is empty
    When I try to add 15 "Pizza Margherita" to my cart
    Then I should receive an error "La quantité maximale par plat est de 10"
    And my cart should remain empty

  Scenario: Handle unavailable dish
    Given there is an unavailable dish "Seasonal Soup"
    When I try to add 1 "Seasonal Soup" to my cart
    Then I should receive an error about dish availability
    And my cart should remain empty

  Scenario: Display cart contents with details
    Given my cart contains various dishes
    When I view my cart details
    Then I should see each dish name, description, unit price, and quantity
    And I should see the subtotal for each dish
    And I should see the overall subtotal, tax, and total

  # US #104 - Error Scenarios for Cart Management
  @error @cart @us-104
  Scenario: Add dish with invalid quantity (zero)
    Given my cart is empty
    When I try to add 0 "Pizza Margherita" to my cart
    Then I should receive an error "Quantity must be greater than 0"
    And my cart should remain empty

  @error @cart @us-104
  Scenario: Add dish with negative quantity
    Given my cart is empty
    When I try to add -2 "Pizza Margherita" to my cart
    Then I should receive an error "Quantity must be greater than 0"
    And my cart should remain empty

  @error @cart @us-104
  Scenario: Remove dish that is not in cart
    Given the system has sample restaurants with dishes
    And I am a registered user
    Given my cart contains 2 "Pizza Margherita"
    When I try to remove "Burger Deluxe" from my cart
    Then I should receive an error "Item 'Burger Deluxe' not found in cart"
    And my cart should still contain 2 "Pizza Margherita"

  @error @cart @us-104
  Scenario: Update dish quantity to invalid amount
    Given my cart contains 2 "Pizza Margherita"
    When I try to update the quantity of "Pizza Margherita" to 0
    Then I should receive an error "Quantity must be greater than 0"
    And my cart should still contain 2 "Pizza Margherita"

  @error @cart @us-104
  Scenario: Add dish from different restaurant to existing cart
    Given my cart contains dishes from "Restaurant A"
    When I try to add a dish from "Restaurant B"
    Then I should receive an error "Cannot mix items from different restaurants"
    And my cart should only contain dishes from "Restaurant A"

  @error @cart @us-104
  Scenario: Exceed maximum cart value
    Given my cart contains expensive dishes totaling 95 euros
    When I try to add a dish worth 10 euros
    Then I should receive an error "Cart total exceeds maximum allowed amount"
    And the expensive dish should not be added to my cart
