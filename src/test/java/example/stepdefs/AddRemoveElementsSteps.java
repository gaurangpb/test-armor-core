package example.stepdefs;

import example.pages.AddRemoveElementsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.testng.Assert;

public class AddRemoveElementsSteps {
    AddRemoveElementsPage addRemoveElementsPage;

    public AddRemoveElementsSteps(AddRemoveElementsPage addRemoveElementsPage) {
        this.addRemoveElementsPage = addRemoveElementsPage;
    }

    @Given("I navigate to the add-remove elements page")
    public void iNavigateToTheAddRemoveElementsPage() {
        addRemoveElementsPage.navigateToPage();
    }

    @When("I click the {string} button {int} times")
    public void iClickTheButtonTimes(String buttonName, int times) {
        addRemoveElementsPage.clickAddElementButton(times);
    }

    @Then("I should see {int} Delete buttons")
    public void iShouldSeeButtons(int count) {
        int buttonCount = addRemoveElementsPage.getDeleteButtonCount();
        Assert.assertEquals(buttonCount, count);
    }

    @When("I click the first Delete button")
    public void iClickTheFirstButton() {
        addRemoveElementsPage.clickFirstDeleteButton();
    }
}
