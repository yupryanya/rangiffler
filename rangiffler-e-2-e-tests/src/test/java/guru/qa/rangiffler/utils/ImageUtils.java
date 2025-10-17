package guru.qa.rangiffler.utils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Base64;

public final class ImageUtils {

  private ImageUtils() {
  }

  public static String toBase64FromClasspath(String imagePath) {
    try (InputStream inputStream = ImageUtils.class.getResourceAsStream("/" + imagePath)) {
      if (inputStream == null) {
        throw new RuntimeException("Image not found in classpath: " + imagePath);
      }

      byte[] imageBytes = inputStream.readAllBytes();
      String mimeType = Files.probeContentType(Path.of(imagePath));

      return "data:" + mimeType + ";base64," + Base64.getEncoder().encodeToString(imageBytes);
    } catch (IOException e) {
      throw new RuntimeException("Failed to read image from classpath: " + imagePath, e);
    }
  }
}