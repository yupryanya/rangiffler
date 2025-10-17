package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.ex.ImageResizeException;
import guru.qa.rangiffler.utils.ImageResizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ImageResizerTest {

  private ImageResizer imageResizer;

  @BeforeEach
  void setUp() {
    imageResizer = new ImageResizer();
  }

  @Test
  void shouldResizeImageWhenHeightGreaterThanTarget() throws Exception {
    BufferedImage original = createTestImage(100, 200, Color.RED);
    String base64 = toBase64Png(original);

    byte[] resizedBytes = imageResizer.resizeToHeight(base64, 100);
    String resizedBase64 = new String(resizedBytes, StandardCharsets.UTF_8);

    assertThat(resizedBase64).startsWith("data:image/png;base64,");
    byte[] decoded = Base64.getDecoder().decode(resizedBase64.split(",", 2)[1]);
    BufferedImage result = ImageIO.read(new java.io.ByteArrayInputStream(decoded));
    assertThat(result.getHeight()).isEqualTo(100);
    assertThat(result.getWidth()).isEqualTo(50); // 100 * 100 / 200
  }

  @Test
  void shouldReturnOriginalIfHeightSmallerOrEqual() throws Exception {
    BufferedImage original = createTestImage(50, 50, Color.BLUE);
    String base64 = toBase64Png(original);

    byte[] result = imageResizer.resizeToHeight(base64, 100);
    String resultStr = new String(result, StandardCharsets.UTF_8);
    assertThat(resultStr).isEqualTo(base64);
  }

  @Test
  void shouldThrowForNullBase64() {
    assertThatThrownBy(() -> imageResizer.resizeToHeight(null, 100))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Base64 image cannot be null");
  }

  @Test
  void shouldThrowForEmptyBase64() {
    assertThatThrownBy(() -> imageResizer.resizeToHeight("  ", 100))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Base64 image cannot be null");
  }

  @Test
  void shouldThrowForNonPositiveHeight() throws Exception {
    BufferedImage original = createTestImage(10, 10, Color.GREEN);
    String base64 = toBase64Png(original);

    assertThatThrownBy(() -> imageResizer.resizeToHeight(base64, 0))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Target height must be positive");

    assertThatThrownBy(() -> imageResizer.resizeToHeight(base64, -5))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Target height must be positive");
  }

  @Test
  void shouldThrowForInvalidBase64Format() {
    String invalid = "not,a,valid,base64";

    assertThatThrownBy(() -> imageResizer.resizeToHeight(invalid, 100))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid base64 image format");
  }

  @Test
  void shouldThrowForInvalidImageData() {
    String invalid = "data:image/png;base64," + Base64.getEncoder().encodeToString("wrong".getBytes());

    assertThatThrownBy(() -> imageResizer.resizeToHeight(invalid, 100))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid image data");
  }

  @Test
  void shouldThrowImageResizeExceptionForUnsupportedFormat() throws Exception {
    BufferedImage original = createTestImage(10, 10, Color.YELLOW);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(original, "png", baos);
    String base64 = "data:image/unsupported;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());

    assertThatThrownBy(() -> imageResizer.resizeToHeight(base64, 5))
        .isInstanceOf(ImageResizeException.class)
        .hasMessageContaining("Could not resize image");
  }

  private String toBase64Png(BufferedImage img) throws Exception {
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    ImageIO.write(img, "png", baos);
    return "data:image/png;base64," + Base64.getEncoder().encodeToString(baos.toByteArray());
  }

  private BufferedImage createTestImage(int width, int height, Color color) {
    BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
    Graphics2D g = img.createGraphics();
    g.setColor(color);
    g.fillRect(0, 0, width, height);
    g.dispose();
    return img;
  }
}