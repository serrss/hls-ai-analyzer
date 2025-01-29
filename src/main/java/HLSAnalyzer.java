public class HLSAnalyzer {

  public static void main(String[] args) {
    String streamUrl = "https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8"; // Replace with your actual URL

    try {
      System.out.println("Downloading M3U8...");
      String m3u8Content = M3U8Downloader.downloadM3U8(streamUrl);
      System.out.println("M3U8 downloaded successfully!");

      System.out.println("Sending to ChatGPT for analysis...");
      String response = ChatGPTClient.analyzeM3U8(m3u8Content);
      System.out.println("ChatGPT Response: \n" + response);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
}
