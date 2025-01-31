import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import org.apache.hc.client5.http.fluent.Request;
import org.apache.hc.client5.http.fluent.Response;
import org.junit.jupiter.api.Test;

public class M3U8DownloaderTest {

  @Test
  void testDownloadValidM3U8() throws Exception {
    // Simulated M3U8 content
    String mockM3U8Content = "#EXTM3U\n#EXT-X-STREAM-INF:BANDWIDTH=1280000\nvideo.m3u8";

    // Mock Content
    org.apache.hc.client5.http.fluent.Content mockContent = mock(org.apache.hc.client5.http.fluent.Content.class);
    when(mockContent.asString()).thenReturn(mockM3U8Content); // Mock `asString()`

    // Mock Response
    Response mockResponse = mock(Response.class);
    when(mockResponse.returnContent()).thenReturn(mockContent);

    // Mock Request
    Request mockRequest = mock(Request.class);
    when(mockRequest.execute()).thenReturn(mockResponse);

    // Call the actual method
    String content = M3U8Downloader.downloadM3U8("https://test-streams.mux.dev/x36xhzz/x36xhzz.m3u8");

    // Validate the response
    assertNotNull(content, "Downloaded content should not be null");
    assertTrue(content.contains("#EXTM3U"), "Content should contain M3U8 header");
  }

  @Test
  void testDownloadInvalidURL() {
    String invalidUrl = "invalid-url";
    assertThrows(IOException.class, () -> M3U8Downloader.downloadM3U8(invalidUrl),
        "Should throw IOException for invalid URL");
  }

  @Test
  void testHttpErrorResponse() throws Exception {
    // Mock request and response
    Response mockResponse = mock(Response.class);
    when(mockResponse.returnContent()).thenThrow(new IOException("Internal Server Error"));

    Request mockRequest = mock(Request.class);
    when(mockRequest.execute()).thenReturn(mockResponse);

    // Expect exception when calling M3U8Downloader
    assertThrows(IOException.class, () -> M3U8Downloader.downloadM3U8("https://example.com/error.m3u8"),
        "Should throw IOException on HTTP error");
  }
}