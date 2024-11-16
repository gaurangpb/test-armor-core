package core.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

public class ConfigReader {
    private static Properties combinedProps = new Properties();

    // Static block to load all properties files
    static {
        loadAllProperties();
    }

    // Load all properties files and combine them
    private static void loadAllProperties() {
        try {
            ClassLoader loader = ConfigReader.class.getClassLoader();
            // Load core properties file
            try (InputStream coreStream = loader.getResourceAsStream("core-config.properties")) {
                if (coreStream != null) {
                    Properties coreProps = new Properties();
                    coreProps.load(coreStream);
                    combinedProps.putAll(coreProps);
                }
            }

            // Load client properties files, if multiple, combine them
            Enumeration<java.net.URL> configFiles = loader.getResources("config.properties");
            while (configFiles.hasMoreElements()) {
                try (InputStream clientStream = configFiles.nextElement().openStream()) {
                    Properties clientProps = new Properties();
                    clientProps.load(clientStream);
                    combinedProps.putAll(clientProps); // Combines all client properties
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Get a property value, with system property override
    public static String getConfigProp(String key) {
        // Check for system property override
        String overriddenValue = System.getProperty(key);
        if (overriddenValue != null) {
            return overriddenValue;
        }
        // Return the value from combined properties
        return combinedProps.getProperty(key);
    }

    // Example convenience method for frequently accessed properties
    public static String getEnvironment() {
        return getConfigProp("environment");
    }

    public static String getBrowser() {
        return getConfigProp("browser");
    }
}