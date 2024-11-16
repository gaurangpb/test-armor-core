package core.hooks;

import core.drivers.SauceLabsManager;
import core.drivers.ScreenshotManager;
import core.drivers.WebDriverManager;
import io.cucumber.java.Scenario;

public abstract class CoreHooks {
    protected final ScreenshotManager screenshotManager;
    protected final WebDriverManager webDriverManager;
    protected final SauceLabsManager sauceLabsManager;  // Renamed from testExecutionManager

    protected CoreHooks() {
        this.screenshotManager = new ScreenshotManager();
        this.webDriverManager = new WebDriverManager();
        this.sauceLabsManager = new SauceLabsManager();
    }

    protected void beforeScenario(Scenario scenario) {
        webDriverManager.initializeDriver(scenario.getName(), scenario.getSourceTagNames());
        sauceLabsManager.logTestDetails(scenario);
    }

    protected void afterScenario(Scenario scenario) {
        try {
            if (scenario.isFailed()) {
                screenshotManager.captureAndAttachScreenshot(scenario);
            }
            sauceLabsManager.updateTestStatus(scenario);
        } finally {
            webDriverManager.quitDriver();
        }
    }
}