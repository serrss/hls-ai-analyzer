import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class ChatGPTClient {
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private static final String API_KEY = ConfigLoader.getProperty("openai.api.key");

  public static String analyzeM3U8(String m3u8Content) throws Exception {
    if (API_KEY == null || API_KEY.isEmpty()) {
      throw new IllegalStateException("OpenAI API key is missing in config.properties!");
    }

    URL url = new URL(API_URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    // Custom Instructions for ChatGPT
    String customInstructions =
        "You are a video streaming analysis program for HLS and DASH. " +
            "You have to analyze streaming files and find all sorts of anomalies. " +
            "Use standard RFC-8216 for validation.\n\n" +
            "If you do not find anomalies, then I expect a response:\n" +
            "{\"status\": \"OK\", \"message\": \"Manifest is correct\"}\n" +
            "If you found anomalies, then I expect a response:\n" +
            "{\"status\": \"FAIL\", \"message\": \"[Add here information about what is wrong in a stream in 200 symbols]\"}";

    // JSON payload for ChatGPT API
    Map<String, Object> data = new HashMap<>();
    data.put("model", "gpt-3.5-turbo");
    data.put("messages", new Object[]{
        new HashMap<String, String>() {{
          put("role", "system");
          put("content", customInstructions);
        }},
        new HashMap<String, String>() {{
          put("role", "user");
          put("content", m3u8Content);
        }}
    });

    // Convert to JSON
    ObjectMapper mapper = new ObjectMapper();
    String jsonPayload = mapper.writeValueAsString(data);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonPayload.getBytes());
    }

    return new String(conn.getInputStream().readAllBytes());
  }
}
