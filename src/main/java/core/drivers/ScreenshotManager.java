package core.drivers;

import io.cucumber.java.Scenario;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ScreenshotManager {
    public void captureAndAttachScreenshot(Scenario scenario) {
        WebDriver driver = WebDriverFactory.getDriver();
        if (driver == null) return;

        String fileName = scenario.getName() + System.currentTimeMillis() / 1000;
        try {
            TakesScreenshot screenshotDriver = (TakesScreenshot) driver;
            File screenshot = screenshotDriver.getScreenshotAs(OutputType.FILE);
            String path = "target/screenshots/" + fileName + ".png";
            FileUtils.copyFile(screenshot, new File(path));
            scenario.attach(Files.readAllBytes(screenshot.toPath()), "image/png", fileName);
        } catch (IOException e) {
            throw new RuntimeException("Screenshot failed: " + e.getMessage());
        }
    }
}