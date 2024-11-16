package core.reports;

import core.config.ConfigReader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportFileUtil {

    private static final String TEST_REPORT_DIR = ConfigReader.getConfigProp("test.report.dir");
    private static final String CUCUMBER_REPORT_PATH = ConfigReader.getConfigProp("cucumber.report.path");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss");

    public static void copyReport() {
        // Get the current timestamp for the file name
        String formattedDateTime = LocalDateTime.now().format(DATE_FORMATTER);

        // Define source and destination paths
        Path source = Paths.get(CUCUMBER_REPORT_PATH);
        Path destinationDirectory = Paths.get(TEST_REPORT_DIR);
        Path destination = destinationDirectory.resolve("cucumber_" + formattedDateTime + ".html");

        try {
            // Ensure destination directory exists
            if (!Files.exists(destinationDirectory)) {
                Files.createDirectories(destinationDirectory);
            }

            // Copy the report file to the destination
            Files.copy(source, destination);

            // Print a clickable link to the copied file
            System.out.println("HTML Report copied successfully: file:///" + destination.toAbsolutePath().toString());
        } catch (IOException e) {
            // Log the error
            e.printStackTrace();
        }
    }
}
