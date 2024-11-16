@ui @login
Feature: Login functionality

  @tc_5
  Scenario: Verify user can log in successfully
    Given I navigate to the login page
    When I enter valid credentials
    And I click the login button
    Then I should see the secure area
