
Feature: CKB2

  Scenario: Toggling checkboxes 5
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked

  Scenario: Toggling checkboxes 6
    Given I am on the checkboxes page
    When I toggle checkbox 1
    Then checkbox 1 should be checked
    When I toggle checkbox 1 again
    Then checkbox 1 should be unchecked
    And checkbox 2 should remain checked

