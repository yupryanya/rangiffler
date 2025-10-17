package guru.qa.rangiffler.utils;

import guru.qa.rangiffler.ex.ImageResizeException;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class ImageResizer {
  public byte[] resizeToHeight(String base64Image, int targetHeight) {
    if (base64Image == null || base64Image.trim().isEmpty()) {
      throw new IllegalArgumentException("Base64 image cannot be null or empty");
    }
    if (targetHeight <= 0) {
      throw new IllegalArgumentException("Target height must be positive");
    }
    if (!base64Image.startsWith("data:") || !base64Image.contains(",")) {
      throw new IllegalArgumentException("Invalid base64 image format");
    }

    try {
      String[] parts = base64Image.split(",", 2);
      if (parts.length != 2) {
        throw new IllegalArgumentException("Invalid base64 image format");
      }
      String prefix = parts[0];
      String base64Data = parts[1];
      String format = prefix.split("/")[1].split(";")[0];

      byte[] imageBytes = Base64.getDecoder().decode(base64Data);
      BufferedImage original = ImageIO.read(new ByteArrayInputStream(imageBytes));
      if (original == null) {
        throw new IllegalArgumentException("Invalid image data");
      }

      int originalHeight = original.getHeight();
      if (originalHeight <= targetHeight) {
        return base64Image.getBytes(StandardCharsets.UTF_8);
      }

      int newWidth = original.getWidth() * targetHeight / originalHeight;
      BufferedImage resized = new BufferedImage(newWidth, targetHeight, BufferedImage.TYPE_INT_RGB);

      Graphics2D g = resized.createGraphics();
      g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      g.drawImage(original, 0, 0, newWidth, targetHeight, null);
      g.dispose();

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      if (!ImageIO.write(resized, format, baos)) {
        throw new IOException("Unsupported image format: " + format);
      }

      String resizedBase64 = Base64.getEncoder().encodeToString(baos.toByteArray());
      return (prefix + "," + resizedBase64).getBytes(StandardCharsets.UTF_8);

    } catch (IOException e) {
      throw new ImageResizeException("Could not resize image due to I/O error");
    }
  }
}