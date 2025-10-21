Feature: Browse restaurants and dishes as a visitor

  As a visitor
  I want to browse restaurants and their available dishes
  So that I can discover what food options are available on campus

  Background:
    Given the system has sample restaurants with dishes

  @smoke
  Scenario: Browse all restaurants without filters
    When I browse restaurants without any filters
    Then I should see all available restaurants
    And each restaurant should display its basic information
    And each restaurant should display its available dishes

  Scenario: Filter restaurants by availability status
    When I browse restaurants filtering by "open" status
    Then I should see only open restaurants
    And all returned restaurants should be open

  @smoke
  Scenario: Filter restaurants by cuisine type
    When I browse restaurants filtering by "VEGETARIAN" cuisine
    Then I should see only restaurants that serve vegetarian dishes
    And each returned restaurant should have at least one vegetarian dish

  Scenario: Filter restaurants by both availability and cuisine type
    When I browse restaurants filtering by "open" status and "MAIN_COURSE" cuisine
    Then I should see only open restaurants that serve main courses
    And all returned restaurants should be open
    And each returned restaurant should have at least one main course dish

  Scenario: Handle empty state when no restaurants match filters
    When I browse restaurants filtering by "STARTER" cuisine
    Then I should see an empty list of restaurants

  Scenario: Display dish details for each restaurant
    When I browse restaurants without any filters
    Then each dish should display its name, description, price and category
    And only available dishes should be displayed

  # US #104 - Error Scenarios for Restaurant Management
  @error @restaurant @us-104
  Scenario: Browse restaurants when system has no restaurants
    Given there are no restaurants in the system
    When I browse restaurants without any filters
    Then I should see an empty list of restaurants
    And the response should indicate no restaurants are available

  @error @restaurant @us-104
  Scenario: Browse restaurants with invalid cuisine filter
    When I browse restaurants filtering by "INVALID_CUISINE" cuisine
    Then I should receive an error about invalid cuisine type
    And I should see an empty list of restaurants

  @error @restaurant @us-104
  Scenario: Browse restaurants when all restaurants are closed
    Given all restaurants in the system are closed
    When I browse restaurants filtering by "open" status
    Then I should see an empty list of restaurants
    And the response should indicate no open restaurants are available

  @error @restaurant @us-104
  Scenario: Handle system error during restaurant browsing
    Given the restaurant system is temporarily unavailable
    When I browse restaurants without any filters
    Then I should receive an error about system unavailability
    And I should be advised to try again later
