package core.drivers;

import core.config.ConfigReader;
import io.cucumber.java.Scenario;
import org.openqa.selenium.JavascriptExecutor;

public class SauceLabsManager {
    public void logTestDetails(Scenario scenario) {
        if (isSauceLabsExecution()) {
            String sauceUrl = "https://app.saucelabs.com/tests/" + WebDriverFactory.getSessionId();
            scenario.log("<a href=" + sauceUrl + " target='_blank'>" + sauceUrl + "</a>");
        }
    }

    public void updateTestStatus(Scenario scenario) {
        if (isSauceLabsExecution()) {
            String query = "sauce:job-result=" + (scenario.isFailed() ? "failed" : "passed");
            ((JavascriptExecutor) WebDriverFactory.getDriver()).executeScript(query);
        }
    }

    private boolean isSauceLabsExecution() {
        return ConfigReader.getConfigProp("runAt").equalsIgnoreCase("saucelabs");
    }
}