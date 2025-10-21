Feature: Order Placement and Confirmation
  As a campus user
  I want to place and confirm orders using my student credit
  So that I can get food delivered to the campus

  Background:
    Given the system has a restaurant "Pizzeria SophiaTech" with available dishes
    And a student user "Marie Dupont" with 50.00€ credit is registered

  Scenario: User successfully places and confirms an order with student credit
    Given the user has added "Pizza Margherita" to their cart
    And the user has added "Salade César" to their cart
    When the user places an order using student credit
    Then the order should be created successfully
    And the order status should be "PAID"
    And the user's credit should be reduced by the order amount
    When the order is confirmed
    Then the order status should be "CONFIRMED"
    And an estimated delivery time should be set
    And the delivery time should be approximately 30 minutes from confirmation

  Scenario: User cannot place order with insufficient credit
    Given the user has added expensive items totaling more than their credit
    When the user attempts to place an order using student credit
    Then the system should display an error about insufficient credit
    And no order should be created

  Scenario: User cannot confirm an already confirmed order
    Given the user has placed and paid for an order
    And the order has been confirmed
    When the user attempts to confirm the order again
    Then the system should display an error about order already confirmed
    And the order status should remain "CONFIRMED"

  Scenario: User cannot place multiple orders simultaneously
    Given the user has an active order in progress
    When the user attempts to place another order
    Then the system should display an error about existing active order
    And the new order should not be created
