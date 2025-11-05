Feature: Add dish to cart and see total

  As a campus user
  I want to add a dish to my cart and see the total price
  So that I can build my order and know how much I'll pay

  Background:
    Given the system has sample restaurants with dishes
    And I am a registered user



  Scenario: Remove dish from cart
    Given my cart contains 2 "Pizza Margherita" and 1 "Caesar Salad"
    When I remove "Pizza Margherita" from my cart
    Then my cart should contain 1 item
    And only "Caesar Salad" should remain in my cart

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
