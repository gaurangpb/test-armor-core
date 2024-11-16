package core.drivers;

import core.config.ConfigReader;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.LocalFileDetector;
import org.openqa.selenium.remote.RemoteWebDriver;
import core.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class WebDriverFactory {
    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();
    private static final ThreadLocal<String> SESSION_ID = new ThreadLocal<>();
    private static final ThreadLocal<TestContext> TEST_CONTEXT = new ThreadLocal<>();
    private static final Logger logger = LoggerFactory.getLogger(WebDriverFactory.class);


    // Prevent instantiation
    private WebDriverFactory() {
        throw new IllegalStateException("Utility class");
    }

    // Context class to hold test-related data
    private static class TestContext {
        private final String name;
        private final Collection<String> tags;
        private final String executionDateTime;

        public TestContext(String name, Collection<String> tags) {
            this.name = name;
            this.tags = tags;
            this.executionDateTime = DateTimeUtil.getCurrentDateTime("yyyyMMdd_HHmm");
        }
    }

    // Browser configuration class
    private static class BrowserConfig {
        private final String browserName;
        private final boolean isMobileEmulation;
        private final String mobileDevice;
        private final boolean isBrowserResize;
        private final Dimension browserDimension;
        private final String chromeVersion;
        private final Map<String, Object> additionalCapabilities;

        private BrowserConfig(Builder builder) {
            this.browserName = builder.browserName;
            this.isMobileEmulation = builder.isMobileEmulation;
            this.mobileDevice = builder.mobileDevice;
            this.isBrowserResize = builder.isBrowserResize;
            this.browserDimension = builder.browserDimension;
            this.chromeVersion = builder.chromeVersion;
            this.additionalCapabilities = builder.additionalCapabilities;
        }

        public static class Builder {
            private final String browserName;
            private boolean isMobileEmulation;
            private String mobileDevice;
            private boolean isBrowserResize;
            private Dimension browserDimension;
            private String chromeVersion;
            private final Map<String, Object> additionalCapabilities = new HashMap<>();

            public Builder(String browserName) {
                this.browserName = browserName;
            }

            public Builder withMobileEmulation(String device) {
                this.isMobileEmulation = true;
                this.mobileDevice = device;
                return this;
            }

            public Builder withBrowserSize(int width, int height) {
                this.isBrowserResize = true;
                this.browserDimension = new Dimension(width, height);
                return this;
            }

            public Builder withChromeVersion(String version) {
                this.chromeVersion = version;
                return this;
            }

            public Builder withCapability(String key, Object value) {
                this.additionalCapabilities.put(key, value);
                return this;
            }

            public BrowserConfig build() {
                return new BrowserConfig(this);
            }
        }
    }

    // SauceLabs configuration class
    private static class SauceLabsConfig {
        private final String username;
        private final String accessKey;
        private final String buildName;
        private final boolean extendedDebugging;
        private final String teamName;
        private final String tunnelName;
        private final String tunnelOwner;

        private SauceLabsConfig() {
            this.username = ConfigReader.getConfigProp("sauce.username");
            this.accessKey = ConfigReader.getConfigProp("sauce.access.key");
            this.buildName = ConfigReader.getConfigProp("buildName").toUpperCase() + "_" + TEST_CONTEXT.get().executionDateTime;
            this.extendedDebugging = Boolean.parseBoolean(ConfigReader.getConfigProp("extendedDebugging"));
            this.teamName = ConfigReader.getConfigProp("sauce.team.name");
            this.tunnelName = ConfigReader.getConfigProp("sauce.tunnel.name");
            this.tunnelOwner = ConfigReader.getConfigProp("sauce.tunnel.owner");
        }

        public Map<String, Object> toCapabilities() {
            Map<String, Object> sauceOptions = new HashMap<>();
            sauceOptions.put("build", buildName);
            sauceOptions.put("name", TEST_CONTEXT.get().name);
            sauceOptions.put("screenResolution", "1920x1080");
            sauceOptions.put("extendedDebugging", extendedDebugging);
            sauceOptions.put("capturePerformance", extendedDebugging);
            sauceOptions.put("public", teamName);
            sauceOptions.put("tunnelName", tunnelName);
            sauceOptions.put("tunnelOwner", tunnelOwner);
            sauceOptions.put("tags", TEST_CONTEXT.get().tags.toArray());
            return sauceOptions;
        }

        public URL getRemoteUrl() {
            try {
                return new URL(String.format("https://%s:%s@ondemand.us-west-1.saucelabs.com:443/wd/hub",
                        username, accessKey));
            } catch (MalformedURLException e) {
                throw new DriverConfigurationException("Invalid SauceLabs URL", e);
            }
        }
    }

    public static class DriverConfigurationException extends RuntimeException {
        public DriverConfigurationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    public static void createDriver() {
        if (DRIVER.get() != null) {
            return;
        }
        BrowserConfig browserConfig = loadBrowserConfig();
        WebDriver driver = createWebDriver(browserConfig);
        DRIVER.set(driver);

        if (driver instanceof RemoteWebDriver) {
            SESSION_ID.set(((RemoteWebDriver) driver).getSessionId().toString());
        }

        if (browserConfig.isBrowserResize) {
            driver.manage().window().setSize(browserConfig.browserDimension);
        }
    }

    private static BrowserConfig loadBrowserConfig() {
        BrowserConfig.Builder builder = new BrowserConfig.Builder(ConfigReader.getConfigProp("browser"));

        if (Boolean.parseBoolean(ConfigReader.getConfigProp("isMobileEmulation"))) {
            builder.withMobileEmulation(ConfigReader.getConfigProp("mobileDevice"));
        }

        if (Boolean.parseBoolean(ConfigReader.getConfigProp("isBrowserResize"))) {
            builder.withBrowserSize(
                    Integer.parseInt(ConfigReader.getConfigProp("desiredWidth")),
                    Integer.parseInt(ConfigReader.getConfigProp("desiredHeight"))
            );
        }

        String chromeVersion = ConfigReader.getConfigProp("overrideChromeVersion");
        if (!chromeVersion.isEmpty()) {
            builder.withChromeVersion(chromeVersion);
        }

        return builder.build();
    }

    private static WebDriver createWebDriver(BrowserConfig config) {
        boolean isRemote = ConfigReader.getConfigProp("runAt").equalsIgnoreCase("saucelabs");

        switch (config.browserName.toLowerCase()) {
            case "chrome":
                return createChromeDriver(config, isRemote);
            case "edge":
                return createEdgeDriver(config, isRemote);
            case "firefox":
                return createFirefoxDriver(config);
            default:
                throw new DriverConfigurationException("Unsupported browser: " + config.browserName, null);
        }
    }

    private static WebDriver createChromeDriver(BrowserConfig config, boolean isRemote) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-cache", "--disable-application-cache", "--remote-allow-origins=*");

        if (config.chromeVersion != null) {
            options.setBrowserVersion(config.chromeVersion);
        }

        if (config.isMobileEmulation) {
            Map<String, String> mobileEmulation = new HashMap<>();
            mobileEmulation.put("deviceName", config.mobileDevice);
            options.setExperimentalOption("mobileEmulation", mobileEmulation);
        }

        if (isRemote) {
            return createRemoteDriver(options);
        }
        return new ChromeDriver(options);
    }

    private static WebDriver createEdgeDriver(BrowserConfig config, boolean isRemote) {
        EdgeOptions options = new EdgeOptions();
        if (isRemote) {
            options.setPlatformName("Windows 10");
            options.setBrowserVersion("latest");
            return createRemoteDriver(options);
        }
        return new EdgeDriver(options);
    }

    private static WebDriver createFirefoxDriver(BrowserConfig config) {
        return new FirefoxDriver();
    }

    private static WebDriver createRemoteDriver(MutableCapabilities options) {
        SauceLabsConfig sauceConfig = new SauceLabsConfig();
        options.setCapability("sauce:options", sauceConfig.toCapabilities());

        RemoteWebDriver driver = new RemoteWebDriver(sauceConfig.getRemoteUrl(), options);
        driver.setFileDetector(new LocalFileDetector());
        return driver;
    }

    // Cleanup method
    public static void quitDriver() {
        WebDriver currentDriver = DRIVER.get();
        try {
            if (currentDriver != null) {
                currentDriver.quit();
            }
        } catch (Exception e) {
           e.printStackTrace();
        } finally {
            DRIVER.remove();
            SESSION_ID.remove();
            TEST_CONTEXT.remove();
        }
    }

    // Getter methods
    public static WebDriver getDriver() {
        createDriver();
        return DRIVER.get();
    }

    public static String getSessionId() {
        return SESSION_ID.get();
    }

    public static void setTestContext(String name, Collection<String> tags) {
        TEST_CONTEXT.set(new TestContext(name, tags));
    }
}