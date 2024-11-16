@ui @AddRemoveElements
Feature: Add and Remove Elements

  @tc_6
  Scenario: Verify user can add and remove elements
    Given I navigate to the add-remove elements page
    When I click the "Add Element" button 3 times
    Then I should see 3 Delete buttons
    When I click the first Delete button
    Then I should see 2 Delete buttons