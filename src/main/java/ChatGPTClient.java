import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatGPTClient {
  private static final String API_URL = "https://api.openai.com/v1/chat/completions";
  private static final String API_KEY = ConfigLoader.getProperty("openai.api.key");

  public static String analyzeM3U8(String m3u8Content, String m3u8Url) throws Exception {
    if (API_KEY == null || API_KEY.isEmpty()) {
      throw new IllegalStateException("OpenAI API key is missing in config.properties!");
    }

    // Check if it's a master playlist and resolve to a variant playlist
    System.out.println("Checking if M3U8 is a master playlist...");
    String variantPlaylistUrl = extractFirstVariantPlaylist(m3u8Content, m3u8Url);
    if (variantPlaylistUrl != null) {
      System.out.println("Master playlist detected. Fetching variant playlist: " + variantPlaylistUrl);
      m3u8Content = M3U8Downloader.downloadM3U8(variantPlaylistUrl);
      m3u8Url = variantPlaylistUrl;
    }

    // Extract first TS segment
    System.out.println("Extracting first .ts segment from M3U8...");
    String tsUrl = extractFirstTsSegment(m3u8Content, m3u8Url);
    if (tsUrl == null) {
      throw new IllegalStateException("No .ts segment found in M3U8 playlist.");
    }

    System.out.println("Downloading .ts segment: " + tsUrl);
    File tsFile = downloadTsSegment(tsUrl);
    System.out.println("Download complete: " + tsFile.getAbsolutePath());

    System.out.println("Running ffprobe on .ts segment...");
    String ffprobeOutput = runFfprobe(tsFile);
    System.out.println("FFprobe analysis complete.");
    System.out.println("FFprobe output:\n" + ffprobeOutput);

    // Custom Instructions for ChatGPT
    String customInstructions =
        "You are a video streaming analysis program for HLS and DASH. " +
            "You have to analyze streaming files and find all sorts of anomalies. " +
            "Use standard RFC-8216 for validation.\n\n" +
            "If you do not find anomalies, then I expect a response:\n" +
            "{\"status\": \"OK\", \"message\": \"Manifest and media file are correct\"}\n" +
            "If you found anomalies, then I expect a response:\n" +
            "{\"status\": \"FAIL\", \"message\": \"[Describe issues in 200 characters]\"}";

    // JSON payload for ChatGPT API
    Map<String, Object> data = new HashMap<>();
    data.put("model", "gpt-3.5-turbo");

    List<Map<String, String>> messages = new ArrayList<>();

    Map<String, String> systemMessage = new HashMap<>();
    systemMessage.put("role", "system");
    systemMessage.put("content", customInstructions);
    messages.add(systemMessage);

    Map<String, String> userMessage = new HashMap<>();
    userMessage.put("role", "user");
    userMessage.put("content", "M3U8 Playlist:\n" + m3u8Content + "\n\nFFprobe Output:\n" + ffprobeOutput);
    messages.add(userMessage);

    data.put("messages", messages);

    // Convert to JSON
    ObjectMapper mapper = new ObjectMapper();
    String jsonPayload = mapper.writeValueAsString(data);

    System.out.println("Sending request to ChatGPT...");
    URL url = new URL(API_URL);
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Authorization", "Bearer " + API_KEY);
    conn.setRequestProperty("Content-Type", "application/json");
    conn.setDoOutput(true);

    try (OutputStream os = conn.getOutputStream()) {
      os.write(jsonPayload.getBytes());
    }

    String response = new String(conn.getInputStream().readAllBytes());
    System.out.println("Received response from ChatGPT.");
    return response;
  }

  private static String extractFirstVariantPlaylist(String m3u8Content, String m3u8Url) {
    Pattern pattern = Pattern.compile("([^#][^\\s]+\\.m3u8)");
    Matcher matcher = pattern.matcher(m3u8Content);

    if (matcher.find()) {
      String variantPlaylist = matcher.group(1);
      if (!variantPlaylist.startsWith("http")) {
        return resolveM3U8Url(m3u8Url, variantPlaylist);
      }
      return variantPlaylist;
    }
    return null;
  }

  private static String extractFirstTsSegment(String m3u8Content, String m3u8Url) {
    Pattern pattern = Pattern.compile("([^#][^\\s]+\\.ts)");
    Matcher matcher = pattern.matcher(m3u8Content);

    if (matcher.find()) {
      String tsSegment = matcher.group(1);
      if (!tsSegment.startsWith("http")) {
        return resolveM3U8Url(m3u8Url, tsSegment);
      }
      return tsSegment;
    }
    return null;
  }

  private static String resolveM3U8Url(String m3u8Url, String relativePath) {
    try {
      URL url = new URL(m3u8Url);
      return new URL(url, relativePath).toString();
    } catch (Exception e) {
      System.err.println("Error resolving URL: " + e.getMessage());
      return null;
    }
  }

  private static File downloadTsSegment(String tsUrl) throws IOException {
    File tempFile = File.createTempFile("segment", ".ts");
    try (BufferedInputStream in = new BufferedInputStream(new URL(tsUrl).openStream());
         FileOutputStream fileOutputStream = new FileOutputStream(tempFile)) {
      byte[] dataBuffer = new byte[1024];
      int bytesRead;
      while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
        fileOutputStream.write(dataBuffer, 0, bytesRead);
      }
    }
    return tempFile;
  }

  private static String runFfprobe(File tsFile) throws IOException {
    ProcessBuilder pb = new ProcessBuilder(
        "ffprobe", "-v", "error", "-show_format", "-show_streams", "-print_format", "json", tsFile.getAbsolutePath());
    Process process = pb.start();

    try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
      StringBuilder output = new StringBuilder();
      String line;
      while ((line = reader.readLine()) != null) {
        output.append(line).append("\n");
      }
      return output.toString();
    }
  }
}