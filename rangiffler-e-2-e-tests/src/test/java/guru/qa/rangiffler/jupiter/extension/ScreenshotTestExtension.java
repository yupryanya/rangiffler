package guru.qa.rangiffler.jupiter.extension;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.jupiter.annotation.ScreenshotTest;
import guru.qa.rangiffler.model.allure.ScreenDiff;
import io.qameta.allure.Allure;
import lombok.SneakyThrows;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class ScreenshotTestExtension implements ParameterResolver, TestExecutionExceptionHandler {
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ScreenshotTestExtension.class);

  private static final ObjectMapper objectMapper = new ObjectMapper();
  private static final Base64.Encoder encoder = Base64.getEncoder();

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return AnnotationSupport.isAnnotated(extensionContext.getRequiredTestMethod(), ScreenshotTest.class) &&
           parameterContext.getParameter().getType().isAssignableFrom(BufferedImage.class);
  }

  @SneakyThrows
  @Override
  public BufferedImage resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    final ScreenshotTest screenshotTest = extensionContext.getRequiredTestMethod().getAnnotation(ScreenshotTest.class);
    return ImageIO.read(
        new ClassPathResource(
            screenshotTest.value()
        ).getInputStream()
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    if (getDiff() == null) {throw throwable;}
    final ScreenshotTest anno = context.getRequiredTestMethod().getAnnotation(ScreenshotTest.class);
    if (anno.rewriteExpected()) {
      ImageIO.write(
          getActual(),
          "png",
          new File("src/test/resources/" + anno.value())
      );
    }

    ScreenDiff screenDif = new ScreenDiff(
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getExpected())),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getActual())),
        "data:image/png;base64," + encoder.encodeToString(imageToBytes(getDiff()))
    );

    Allure.addAttachment(
        "Screenshot diff",
        "application/vnd.allure.image.diff",
        objectMapper.writeValueAsString(screenDif)
    );
    throw throwable;
  }

  public static void setExpected(BufferedImage expected) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("expected", expected);
  }

  public static BufferedImage getExpected() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("expected", BufferedImage.class);
  }

  public static void setActual(BufferedImage actual) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("actual", actual);
  }

  public static BufferedImage getActual() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("actual", BufferedImage.class);
  }

  public static void setDiff(BufferedImage diff) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("diff", diff);
  }

  public static BufferedImage getDiff() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("diff", BufferedImage.class);
  }

  private static byte[] imageToBytes(BufferedImage image) {
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
      ImageIO.write(image, "png", outputStream);
      return outputStream.toByteArray();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
