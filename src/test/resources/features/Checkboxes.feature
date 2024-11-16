@ui
Feature: Checkbox interaction on the-internet.herokuapp.com

  Scenario: Toggling checkboxes 1
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked


  Scenario: Toggling checkboxes 2
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked


  Scenario: Toggling checkboxes 3
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked

  Scenario: Toggling checkboxes 4
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked
