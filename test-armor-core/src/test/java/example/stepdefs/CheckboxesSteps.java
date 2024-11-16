package example.stepdefs;

import example.pages.CheckboxesPage;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

import java.nio.charset.StandardCharsets;

public class CheckboxesSteps {

    CheckboxesPage checkboxesPage;

    public CheckboxesSteps(CheckboxesPage page) {
        checkboxesPage = page;
    }

    Scenario scenario;
    @Before
    public void before(Scenario scenario) {
        this.scenario = scenario;
    }

    @Given("I am on the checkboxes page")
    public void i_am_on_the_checkboxes_page() {
        checkboxesPage.open();
    }

    @When("I toggle checkbox 1")
    public void i_toggle_checkbox_1() {
        checkboxesPage.toggleCheckbox1();
    }

    @When("I toggle checkbox 1 again")
    public void i_toggle_checkbox_1_again() {
        checkboxesPage.toggleCheckbox1();
        // 3. Attach JSON data
        String jsonContent = "{\"key\": \"value\", \"status\": \"passed\"}";
        scenario.attach(jsonContent.getBytes(StandardCharsets.UTF_8),
                "application/json",
                "JSON Data");
    }

    @Then("checkbox 1 should be checked")
    public void checkbox_1_should_be_checked() {
        Assert.assertTrue(checkboxesPage.isCheckbox1Selected());
    }

    @Then("checkbox 1 should be unchecked")
    public void checkbox_1_should_be_unchecked() {
        Assert.assertFalse(checkboxesPage.isCheckbox1Selected());
    }

    @Then("checkbox 2 should remain checked")
    public void checkbox_2_should_remain_checked() {
        Assert.assertTrue(checkboxesPage.isCheckbox2Selected());
    }
}
