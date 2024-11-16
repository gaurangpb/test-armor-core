package core.util;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestContext {
    private final Map<String, Object> context = new ConcurrentHashMap<>();

    public <T> T getContext(String key) {
        return (T) context.get(key);
    }

    public void setContext(String key, Object value) {
        this.context.put(key, value);
    }

    // Clear method to reset context between scenarios
    public void clearContext() {
        context.clear();
    }
}
