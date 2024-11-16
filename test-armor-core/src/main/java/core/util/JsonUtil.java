package core.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class JsonUtil {
    private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // Utility method to load a resource as an InputStream
    private static InputStream loadResource(String path) throws IOException {
        InputStream inputStream = JsonUtil.class.getClassLoader().getResourceAsStream(path);
        if (inputStream == null) {
            throw new IOException("File not found: " + path);
        }
        return inputStream;
    }

    /**
     * Reads a JSON array from the file and returns it as a string.
     *
     * @param fullPath the path of the JSON array file
     * @return JSON array as a string or null if error occurs
     */
    public static String getJsonArrayFileAsString(String fullPath) {
        try (InputStream inputStream = loadResource(fullPath)) {
            // Read JSON array from the file using Jackson
            JsonNode jsonNode = objectMapper.readTree(inputStream);

            if (!jsonNode.isArray()) {
                throw new IOException("File does not contain a JSON array");
            }

            return jsonNode.toString();
        } catch (Exception e) {
            logger.error("Error reading JSON array from file: " + fullPath, e);
            return null;
        }
    }

    /**
     * Retrieves a specific value from a JSON file located in the "data" directory.
     *
     * @param fileName the name of the JSON file
     * @param module   the module name inside the JSON file
     * @param key      the key whose value is to be retrieved
     * @return the value as a string or Optional.empty() if not found
     */
    public static Optional<String> getJsonDataFromFile(String fileName, String module, String key) {
        try (InputStream inputStream = loadResource("data/" + fileName)) {
            JsonNode rootNode = objectMapper.readTree(inputStream);

            JsonNode moduleNode = rootNode.path(module);
            if (moduleNode.isMissingNode()) {
                return Optional.empty();
            }

            JsonNode valueNode = moduleNode.path(key);
            if (valueNode.isMissingNode()) {
                return Optional.empty();
            }

            return Optional.of(valueNode.asText());
        } catch (IOException e) {
            logger.error("Error occurred while reading the JSON file: " + fileName, e);
            return Optional.empty();
        }
    }

    /**
     * Additional utility method to parse JSON string to JsonNode
     *
     * @param jsonString the JSON string to parse
     * @return Optional<JsonNode> containing the parsed JSON or empty if parsing fails
     */
    public static Optional<JsonNode> parseJson(String jsonString) {
        try {
            return Optional.of(objectMapper.readTree(jsonString));
        } catch (IOException e) {
            logger.error("Error parsing JSON string", e);
            return Optional.empty();
        }
    }

    /**
     * Additional utility method to convert object to JSON string
     *
     * @param object the object to convert
     * @return Optional<String> containing the JSON string or empty if conversion fails
     */
    public static Optional<String> toJson(Object object) {
        try {
            return Optional.of(objectMapper.writeValueAsString(object));
        } catch (IOException e) {
            logger.error("Error converting object to JSON string", e);
            return Optional.empty();
        }
    }
}