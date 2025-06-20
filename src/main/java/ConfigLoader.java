import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigLoader {
  private static final String CONFIG_FILE = "config.properties";
  private static final Properties properties = new Properties();

  // Static block to load properties once
  static {
    try (InputStream input = ConfigLoader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
      if (input == null) {
        throw new IOException("Config file not found: " + CONFIG_FILE);
      }
      properties.load(input);
    } catch (IOException e) {
      throw new RuntimeException("Failed to load configuration: " + e.getMessage(), e);
    }
  }

  /**
   * Get a property value by key.
   *
   * @param key the property key
   * @return the property value
   */
  public static String getProperty(String key) {
    return properties.getProperty(key);
  }

  public static String getProjectId() {
    return getProperty("openai.project.id", "");
  }

  /**
   * Get a property value with a default fallback.
   *
   * @param key the property key
   * @param defaultValue the default value if the key is missing
   * @return the property value or default value
   */
  public static String getProperty(String key, String defaultValue) {
    return properties.getProperty(key, defaultValue);
  }
}
