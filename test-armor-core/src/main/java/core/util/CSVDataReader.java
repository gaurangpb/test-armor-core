package core.util;

import com.opencsv.CSVReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class CSVDataReader {

    /**
     * Reads data from a CSV file and returns it as a list of String arrays.
     *
     * @param csvFilePath the relative path to the CSV file located in the resources folder
     * @return a list of String arrays containing the data from the CSV file
     */
    public static List<String[]> getCSVFileData(String csvFilePath) {
        List<String[]> data = new ArrayList<>();

        // Try-with-resources to ensure proper resource management
        try (InputStream inputStream = CSVDataReader.class.getClassLoader().getResourceAsStream(csvFilePath);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             CSVReader csvReader = new CSVReader(reader)) {

            String[] line;
            while ((line = csvReader.readNext()) != null) {
                data.add(line);
            }
        } catch (Exception e) {
            // Handle the exception (e.g., log it) if needed
            e.printStackTrace();
        }

        return data;
    }
}
