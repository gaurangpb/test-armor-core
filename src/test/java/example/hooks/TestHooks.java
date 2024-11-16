package example.hooks;

import core.hooks.CoreHooks;
import core.reports.ReportFileUtil;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.testng.annotations.AfterSuite;

public class TestHooks extends CoreHooks {

    @Before("@ui")
    public void setupUI(Scenario scenario) {
        beforeScenario(scenario);
    }

    @After
    public void tearDownUI(Scenario scenario) {
        afterScenario(scenario);
    }

    @Before("@api")
    public void setupAPI(Scenario scenario) {
        // API test setup if needed
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
       //execute after everything completes
        ReportFileUtil.copyReport();
    }
}
