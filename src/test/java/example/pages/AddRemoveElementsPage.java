package example.pages;

import core.base.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class AddRemoveElementsPage extends PageObject {

    // Locators
    private final By addElementButton = By.cssSelector(".example button");
    private final By deleteButtons = By.cssSelector(".added-manually");

    // Methods
    public void navigateToPage() {
        driver.get("https://the-internet.herokuapp.com/add_remove_elements/");
    }

    public void clickAddElementButton(int times) {
        for (int i = 0; i < times; i++) {
            click(addElementButton);
        }
    }

    public int getDeleteButtonCount() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(10000));
        List<WebElement> buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(deleteButtons));
        return buttons.size();
    }

    public void clickFirstDeleteButton() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        List<WebElement> buttons = wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(deleteButtons));
        if (!buttons.isEmpty()) {
            buttons.get(0).click();
        }
    }
}
