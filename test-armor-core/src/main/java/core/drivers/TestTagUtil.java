package core.drivers;

import io.cucumber.java.Scenario;

public class TestTagUtil {
    public static String extractTestCaseTag(Scenario scenario) {
        return scenario.getSourceTagNames().stream().filter(tag -> tag.startsWith("@tc_")).findFirst().orElse("");
    }

    // Add other tag-related utility methods here
}