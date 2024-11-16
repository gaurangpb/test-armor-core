package core.base;

import core.config.ConfigReader;
import core.drivers.WebDriverFactory;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Enhanced Page Object class providing robust web automation capabilities
 * with improved error handling, logging, and additional functionality.
 */
public class PageObject {
    private static final Logger logger = LoggerFactory.getLogger(PageObject.class);
    protected final WebDriver driver;
    private final int implicitWaitTime;
    private final int pageLoadTimeout;

    private static final int DEFAULT_RETRY_COUNT = 3;
    private static final Duration POLLING_INTERVAL = Duration.ofMillis(500);

    /**
     * Constructor initializing WebDriver and configuration settings
     */
    public PageObject() {
        this.driver = WebDriverFactory.getDriver();
        this.implicitWaitTime = Integer.parseInt(ConfigReader.getConfigProp("implicitWaitTime"));
        this.pageLoadTimeout = Integer.parseInt(ConfigReader.getConfigProp("pageLoadTimeout"));

        configureDriver();
    }

    // region Driver Configuration

    private void configureDriver() {
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
        driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(pageLoadTimeout));

        if (!Boolean.parseBoolean(ConfigReader.getConfigProp("isBrowserResize"))) {
            driver.manage().window().maximize();
        }
    }

    public void setCookies(Cookie cookie) {
        driver.manage().addCookie(cookie);
    }

    public void refreshBrowserWindow() {
        driver.navigate().refresh();
    }

    // endregion

    // region Element Finders

    /**
     * Shorthand method to find element
     */
    public WebElement $(By by) {
        return findElement(by);
    }

    /**
     * Shorthand method to find elements
     */
    public List<WebElement> $$(By by) {
        return driver.findElements(by);
    }

    /**
     * Finds elements without implicit wait
     */
    public List<WebElement> findElementsWithoutWait(By by) {
        driver.manage().timeouts().implicitlyWait(Duration.ofMillis(0));
        try {
            return driver.findElements(by);
        } finally {
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(implicitWaitTime));
        }
    }

    // endregion

    // region Wait Operations

    /**
     * Waits for element with custom timeout and condition
     */
    public WebElement waitFor(By locator, Duration timeout, Function<By, WebElement> condition) {
        WebDriverWait wait = new WebDriverWait(driver, timeout, POLLING_INTERVAL);
        return wait.until(driver -> {
            try {
                return condition.apply(locator);
            } catch (StaleElementReferenceException e) {
                logger.debug("Stale element, retrying...");
                return null;
            }
        });
    }

    public void waitForElementVisible(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitTime));
        wait.until(ExpectedConditions.visibilityOfElementLocated(by));
    }

    public void waitForElementInvisible(By by, int timeoutInSeconds) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(timeoutInSeconds), POLLING_INTERVAL);
        wait.until(driver -> findElementsWithoutWait(by).isEmpty());
    }

    public void waitForElementInvisible(By by) {
        waitForElementInvisible(by, implicitWaitTime);
    }

    public WebElement waitForElementTOBeClickable(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitTime));
        return wait.until(ExpectedConditions.elementToBeClickable(by));
    }


    public WebElement waitForPresenceOfElement(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitTime));
        wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(by));
        return $(by);
    }

    public boolean waitForUrlContains(String url) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.urlContains(url));
    }

    public boolean waitForUrlToBe(String url) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        return wait.until(ExpectedConditions.urlToBe(url));
    }

    /**
     * Smart wait using dynamic conditions instead of Thread.sleep
     */
    private void smartWait(int milliseconds) {
        new WebDriverWait(driver, Duration.ofMillis(milliseconds))
                .pollingEvery(Duration.ofMillis(50))
                .until(webDriver -> true);
    }

    // endregion

    // region Element Operations

    /**
     * Finds element with retry mechanism and logging
     */
    public WebElement findElement(By by) {
        return retry(() -> {
            logger.debug("Finding element: {}", by);
            return driver.findElement(by);
        }, "Failed to find element: " + by);
    }

    /**
     * Performs click action with retry and error handling
     */
    public void click(By by) {
        retry(() -> {
            WebElement element = waitForElementTOBeClickable(by);
            try {
                element.click();
            } catch (ElementClickInterceptedException e) {
                logger.debug("Click intercepted, trying JavaScript click");
                jsClick(element);
            }
            return null;
        }, "Failed to click element: " + by);
    }

    public void click(WebElement element) {
        scrollIntoView(element);
        element.click();
    }

    public String click(By by, int index) {
        List<WebElement> elements = $$(by);
        String value = elements.get(index).getText();
        elements.get(index).click();
        return value;
    }

    public void jsClick(By by) {
        WebElement element = $(by);
        jsClick(element);
    }

    public void jsClick(WebElement element) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].click();", element);
    }

    public void JSEnterText(By by, String text) {
        JavascriptExecutor executor = (JavascriptExecutor) driver;
        executor.executeScript("arguments[0].setAttribute('value', '" + text + "')", $(by));
    }

    public String getText(By by) {
        return $(by).getText();
    }

    /**
     * Types text with customizable speed and verification
     */
    public void typeText(By by, String text, int delayMillis) {
        WebElement element = waitForElementTOBeClickable(by);
        element.clear();

        if (delayMillis > 0) {
            for (char c : text.toCharArray()) {
                element.sendKeys(String.valueOf(c));
                smartWait(delayMillis);
            }
        } else {
            element.sendKeys(text);
        }

        // Verify text was entered correctly
        String actualText = element.getAttribute("value");
        if (!actualText.equals(text)) {
            logger.warn("Text verification failed. Expected: {}, Actual: {}", text, actualText);
            element.clear();
            element.sendKeys(text);
        }
    }

    public void clearAndSendKeys(By by, CharSequence... keysToSend) {
        WebElement element = waitForElementTOBeClickable(by);
        element.clear();
        element.sendKeys(keysToSend);
    }

    public void slowType(By by, int slownessInMillis, String keysToSend) {
        typeText(by, keysToSend, slownessInMillis);
    }

    public void scrollIntoView(By by) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", $(by));
    }

    public void scrollIntoView(WebElement element) {
        JavascriptExecutor js = (JavascriptExecutor) driver;
        js.executeScript("arguments[0].scrollIntoView(true);", element);
    }

    public void hoverOnElement(WebElement element) {
        Actions actions = new Actions(driver);
        actions.moveToElement(element).perform();
    }

    public boolean isElementClickable(By by) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofMillis(500));
        try {
            return wait.until(ExpectedConditions.elementToBeClickable(by)) != null;
        } catch (Exception e) {
            return false;
        }
    }

    // endregion

    // region Window and Frame Handling

    /**
     * Handles multiple windows with custom action execution
     */
    public void handleMultipleWindows(Action action) {
        String originalHandle = driver.getWindowHandle();
        try {
            Set<String> handles = driver.getWindowHandles();
            for (String handle : handles) {
                if (!handle.equals(originalHandle)) {
                    driver.switchTo().window(handle);
                    action.execute();
                    driver.close();
                }
            }
        } finally {
            driver.switchTo().window(originalHandle);
        }
    }

    public void switchToFrame(By by) {
        driver.switchTo().frame($(by));
    }

    public void switchToDefaultContent() {
        driver.switchTo().defaultContent();
    }

    public boolean isNewTabOpen() {
        String currentWindowHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        return handles.stream().anyMatch(handle -> !handle.equals(currentWindowHandle));
    }

    public String switchToNewTab() {
        String currentWindowHandle = driver.getWindowHandle();
        Set<String> handles = driver.getWindowHandles();
        handles.stream()
                .filter(handle -> !handle.equals(currentWindowHandle))
                .findFirst()
                .ifPresent(handle -> driver.switchTo().window(handle));
        return currentWindowHandle;
    }

    public void switchToTab(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public void waitForNewTab() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitTime));
        wait.until(driver -> isNewTabOpen());
    }


    public void executeInNewTab(String url) {
        String originalHandle = driver.getWindowHandle();
        ((JavascriptExecutor) driver).executeScript("window.open('" + url + "');");
        Set<String> windowHandles = driver.getWindowHandles();
        windowHandles.stream()
                .filter(handle -> !handle.equals(originalHandle))
                .findFirst()
                .ifPresent(handle -> driver.switchTo().window(handle));
    }

    public boolean waitForTabAction(Action condition) {
        smartWait(6000);
        String originalHandle = driver.getWindowHandle();
        Set<String> windowHandles = driver.getWindowHandles();
        if (windowHandles.size() < 2) {
            throw new NoSuchWindowException("waitForTabAction(): No new tab is open.");
        }

        try {
            windowHandles.stream()
                    .filter(handle -> !handle.equals(originalHandle))
                    .findFirst()
                    .ifPresent(handle -> driver.switchTo().window(handle));
            condition.execute();
            return true;
        } catch (Exception e) {
            logger.error("Error in tab action: ", e);
            takeScreenshot(System.currentTimeMillis() + "_tab_action_error");
            return false;
        } finally {
            driver.close();
            driver.switchTo().window(originalHandle);
            smartWait(500);
        }
    }

    // endregion

    // region Alert Handling

    /**
     * Handles different types of alerts
     */
    public void handleAlert(AlertAction action) {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(implicitWaitTime));
        try {
            Alert alert = wait.until(ExpectedConditions.alertIsPresent());
            switch (action) {
                case ACCEPT:
                    alert.accept();
                    break;
                case DISMISS:
                    alert.dismiss();
                    break;
                case GET_TEXT:
                    logger.info("Alert text: {}", alert.getText());
                    break;
            }
        } catch (TimeoutException e) {
            logger.warn("No alert present after waiting {} seconds", implicitWaitTime);
        }
    }

    public enum AlertAction {
        ACCEPT, DISMISS, GET_TEXT
    }

    // endregion

    // region Screenshots and Logging

    /**
     * Takes screenshot with enhanced error handling and file naming
     *
     * @param filePath The full file path for the screenshot, including the file name and extension
     */
    public void takeScreenshot(String filePath) {
        try {
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
            File screenshotFile = new File(filePath);
            FileUtils.copyFile(screenshot, screenshotFile);
            logger.info("Screenshot saved: {}", filePath);
        } catch (IOException e) {
            logger.error("Failed to take screenshot: {}", e.getMessage());
            throw new RuntimeException("Screenshot failed", e);
        }
    }
    // region State Checking

    /**
     * Checks if element is visible without waiting
     */
    public boolean isVisible(By by) {
        return !findElementsWithoutWait(by).isEmpty();
    }

    /**
     * Checks if element exists in DOM
     */
    public boolean elementExists(By by) {
        try {
            return !findElementsWithoutWait(by).isEmpty();
        } catch (Exception e) {
            return false;
        }
    }

    // endregion

    // region Utility Methods

    /**
     * Generic retry mechanism for handling flaky operations
     */
    private <T> T retry(SupplierWithException<T> action, String errorMessage) {
        Exception lastException = null;
        for (int i = 0; i < DEFAULT_RETRY_COUNT; i++) {
            try {
                return action.get();
            } catch (Exception e) {
                lastException = e;
                logger.warn("Attempt {} failed: {}", i + 1, e.getMessage());
                smartWait(1000 * (i + 1)); // Exponential backoff
            }
        }
        throw new RuntimeException(errorMessage, lastException);
    }

    @FunctionalInterface
    private interface SupplierWithException<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface Action {
        void execute();
    }

    /**
     * Executes JavaScript code
     */
    public Object executeJavaScript(String script, Object... args) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            return js.executeScript(script, args);
        } catch (Exception e) {
            logger.error("Failed to execute JavaScript: {}", script, e);
            throw new RuntimeException("JavaScript execution failed", e);
        }
    }

    /**
     * Gets page source
     */
    public String getPageSource() {
        return driver.getPageSource();
    }

    /**
     * Gets current URL
     */
    public String getCurrentUrl() {
        return driver.getCurrentUrl();
    }

    /**
     * Gets page title
     */
    public String getTitle() {
        return driver.getTitle();
    }

    // endregion

    // region Browser Management

    /**
     * Closes current window
     */
    public void closeWindow() {
        driver.close();
    }

    /**
     * Quits browser and cleans up resources
     */
    public void closeBrowser() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception e) {
                logger.error("Error while closing browser: {}", e.getMessage());
            }
        }
    }

    /**
     * Cleans up resources
     */
    public void cleanup() {
        try {
            if (driver != null) {
                driver.quit();
            }
        } catch (Exception e) {
            logger.error("Error during cleanup: {}", e.getMessage());
        }
    }

    /**
     * Clears browser cookies
     */
    public void clearCookies() {
        driver.manage().deleteAllCookies();
    }

    // endregion

    // region Element State Verification

    /**
     * Verifies if element is displayed with timeout
     */
    public boolean isDisplayed(By by, Duration timeout) {
        try {
            WebDriverWait wait = new WebDriverWait(driver, timeout);
            return wait.until(driver -> $(by).isDisplayed());
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies if element is enabled
     */
    public boolean isEnabled(By by) {
        try {
            return $(by).isEnabled();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Verifies if element is selected
     */
    public boolean isSelected(By by) {
        try {
            return $(by).isSelected();
        } catch (Exception e) {
            return false;
        }
    }

    // endregion

    // region Element Attributes and Properties

    /**
     * Gets element attribute
     */
    public String getAttribute(By by, String attributeName) {
        return $(by).getAttribute(attributeName);
    }

    /**
     * Gets element CSS value
     */
    public String getCssValue(By by, String propertyName) {
        return $(by).getCssValue(propertyName);
    }

    /**
     * Gets element location
     */
    public Point getLocation(By by) {
        return $(by).getLocation();
    }

    /**
     * Gets element size
     */
    public Dimension getSize(By by) {
        return $(by).getSize();
    }

    // endregion

    // region Keyboard and Mouse Actions

    /**
     * Performs keyboard actions
     */
    public void pressKey(By by, Keys key) {
        $(by).sendKeys(key);
    }

    /**
     * Performs complex mouse actions
     */
    public void performMouseAction(Consumer<Actions> actionSequence) {
        Actions actions = new Actions(driver);
        actionSequence.accept(actions);
        actions.perform();
    }

    /**
     * Double clicks on element
     */
    public void doubleClick(By by) {
        Actions actions = new Actions(driver);
        actions.doubleClick($(by)).perform();
    }

    /**
     * Right clicks on element
     */
    public void rightClick(By by) {
        Actions actions = new Actions(driver);
        actions.contextClick($(by)).perform();
    }

    /**
     * Drags and drops element
     */
    public void dragAndDrop(By source, By target) {
        Actions actions = new Actions(driver);
        actions.dragAndDrop($(source), $(target)).perform();
    }

    // endregion
}
