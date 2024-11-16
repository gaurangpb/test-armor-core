package example.pages;

import core.base.PageObject;
import org.openqa.selenium.By;
import org.testng.Assert;

import java.time.Duration;

public class LoginPage extends PageObject {


    // Locators
    private By usernameField = By.id("username");
    private By passwordField = By.id("password");
    private By loginButton = By.cssSelector("button[type='submit']");
    private By successMessage = By.cssSelector(".flash.success");

    // Methods
    public void navigateToLoginPage() {
        driver.get("https://the-internet.herokuapp.com/login");
    }

    public void enterUsername(String username) {
        clearAndSendKeys(usernameField, username);
    }

    public void enterPassword(String password) {
        clearAndSendKeys(passwordField, password);
    }

    public void clickLoginButton() {
        click(loginButton);
    }

    public void verifySecureAreaDisplayed() {

        Assert.assertTrue(isDisplayed(successMessage, Duration.ofSeconds(5)), "Secure area not displayed!");
        Assert.assertTrue(getText(successMessage).contains("You logged into a secure area!"), "Login message incorrect!");
    }
}
