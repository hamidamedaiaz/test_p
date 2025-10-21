Feature: End-to-end checkout flow
  As a registered campus user
  I want to order a dish, choose a delivery slot and pay
  So that my order is confirmed only if payment succeeds

  Background:
    Given a clean application state
    And the campus has a restaurant "Pizzeria SophiaTech" with dishes:
      | name             | description                                  | price | category      | available |
      | Pizza Margherita | Pizza classique avec tomate/mozza/basilic     | 12.50 | MAIN_COURSE   | true      |
      | Salade César     | Salade fraîche avec poulet grillé et parmesan | 9.00  | STARTER       | true      |
    And a registered user "Marie Dupont" with email "etudiant.demo@unice.fr" and student credit "50.00" EUR
    And the restaurant "Pizzeria SophiaTech" exposes the following half-hour capacities:
      | from  | to    | capacity |
      | 11:00 | 11:30 | 5        |
      | 11:30 | 12:00 | 10       |
    And external payments are handled by a test double gateway

  @e2e @external
  Scenario: Successful payment with external gateway
    Given the external payment gateway will "ACCEPT"
    When the user browses restaurants with no filters
    And the user selects restaurant "Pizzeria SophiaTech"
    And the user adds dish "Salade César" quantity 1 to the cart
    And the user chooses the next available delivery slot
    And the user pays using "EXTERNAL"
    Then the order is created with status "PENDING"
    And the user sees the payment method recorded as "EXTERNAL_CARD"
    When the payment provider confirms the payment for the last order
    Then the order eventually has status "PAID"



  @e2e @external
  Scenario: Successful payment with external gateway
    Given the external payment gateway will "ACCEPT"
    When the user browses restaurants with no filters
    And the user selects restaurant "Pizzeria SophiaTech"
    And the user adds dish "Salade César" quantity 1 to the cart
    And the user chooses the next available delivery slot
    And the user pays using "EXTERNAL"
    Then the order is created with status "PAID"
    And the user sees the payment method recorded as "EXTERNAL"

  @e2e @decline
  Scenario: Failed payment with external gateway
    Given the external payment gateway will "DECLINE"
    When the user browses restaurants with no filters
    And the user selects restaurant "Pizzeria SophiaTech"
    And the user adds dish "Pizza Margherita" quantity 1 to the cart
    And the user chooses the next available delivery slot
    And the user pays using "EXTERNAL"
    Then the order is created with status "PENDING"
    And the user sees the payment method recorded as "EXTERNAL_CARD"
    # Optionnel quand tu auras le callback :
    # When the payment provider declines the payment for the last order with reason "DECLINED"
    # Then the order eventually has status "PAYMENT_FAILED"
    # And the user's cart is not cleared

