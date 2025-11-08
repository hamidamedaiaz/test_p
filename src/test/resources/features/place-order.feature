Feature: Place a complete order
  As a customer
  I want to be able to create a restaurant, add dishes, place an order and pay
  So that I can receive my favorite meals without any issues

  Background:
    Given an administrator initializes the system with a clean in-memory repository

  Scenario Outline: Create a restaurant and place an order through to payment
    Given an administrator creates a restaurant named "Chez Diop" with the following dishes:
      | dish          | description | price | status     |
      | Thieboudieune | ""          | 12    | available  |
      | Poulet Yassa  | ""          | 10    | available  |
      | Mafe          | ""          | 9     | available  |
    And a CampusUser "Seye" is registered with a balance of "50" euros
    And CampusUser "Seye" adds to the cart from restaurant "Chez Diop":
      | dish          | quantity |
      | Thieboudieune | 1        |
      | Poulet Yassa  | 2        |
    When CampusUser "Seye" places the order for the slot "<slot_time>"
    Then the order is successfully created
    And the total amount of the order is "32" euros
    When CampusUser "Seye" proceeds to payment
    Then the payment is successful
    And the CampusUser balance is updated to "18" euros
    And the order status is "Paid"
    Examples:
      | slot_time |
      | 2030-10-20 12:00 |

  Scenario: Payment declined due to insufficient funds
    Given a restaurant "Chez Diop" exists with the following dishes:
      | dish          | description | price | status     |
      | Thieboudieune | ""          | 12    | available  |
    And a CampusUser "Amadou" is registered with a balance of "5" euros
    And CampusUser "Amadou" adds to the cart from restaurant "Chez Diop":
      | dish          | quantity |
      | Thieboudieune | 1        |
    When CampusUser "Amadou" places the order for the slot "2030-10-20 13:00"
    And CampusUser "Amadou" proceeds to payment
    Then an error "Insufficient credit" is returned
    And the order is not paid

  Scenario: Order refused because a dish is unavailable
    Given a restaurant "Chez Diop" exists with the following dishes:
      | dish   | description | price | status      |
      | Mafe   | ""          | 9     | available   |
      | Yassa  | ""          | 10    | unavailable |
    And a CampusUser "Fatou" is registered with a balance of "40" euros
    When CampusUser "Fatou" adds to the cart from restaurant "Chez Diop":
      | dish  | quantity |
      | Yassa | 1        |
    Then an error "Le plat n'est pas disponible: Yassa" is returned
    And the order is not created
