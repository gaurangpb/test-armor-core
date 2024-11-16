package core.drivers;

import org.openqa.selenium.WebDriver;

import java.util.Collection;

public class WebDriverManager {
    public void initializeDriver(String testName, Collection<String> tags) {
        WebDriverFactory.setTestContext(testName, tags);
        WebDriverFactory.createDriver();
    }

    public String getSessionId() {
        return WebDriverFactory.getSessionId();
    }

    public WebDriver getDriver() {
        return WebDriverFactory.getDriver();
    }

    public void quitDriver() {
        WebDriverFactory.quitDriver();
    }
}