import java.io.IOException;
import org.apache.hc.client5.http.fluent.Request;

public class M3U8Downloader {
  public static String downloadM3U8(String url) throws IOException {
    return Request.get(url)
        .execute()
        .returnContent()
        .asString();
  }
}
