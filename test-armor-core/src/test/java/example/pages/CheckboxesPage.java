package example.pages;

import core.base.PageObject;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public class CheckboxesPage extends PageObject {

    // Locators for checkboxes
    private By checkbox1Locator = By.cssSelector("#checkboxes input:nth-of-type(1)");
    private By checkbox2Locator = By.cssSelector("#checkboxes input:nth-of-type(2)");

    // Navigate to checkboxes page
    public void open() {
        driver.get("https://the-internet.herokuapp.com/checkboxes");
    }

    // Toggle checkbox 1
    public void toggleCheckbox1() {
        WebElement checkbox1 = driver.findElement(checkbox1Locator);
        checkbox1.click();
    }

    // Check if checkbox 1 is selected
    public boolean isCheckbox1Selected() {
        return driver.findElement(checkbox1Locator).isSelected();
    }

    // Check if checkbox 2 is selected
    public boolean isCheckbox2Selected() {
        return driver.findElement(checkbox2Locator).isSelected();
    }
}
