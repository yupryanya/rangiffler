package guru.qa.rangiffler.utils;

import guru.qa.rangiffler.jupiter.extension.ScreenshotTestExtension;
import ru.yandex.qatools.ashot.comparison.ImageDiff;
import ru.yandex.qatools.ashot.comparison.ImageDiffer;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.image.BufferedImage;
import java.util.function.BooleanSupplier;

@ParametersAreNonnullByDefault
public class ScreenDiffResult implements BooleanSupplier {
  private final BufferedImage expected;
  private final BufferedImage actual;
  private final ImageDiff diff;
  private final boolean hasDiff;

  public ScreenDiffResult(BufferedImage expected, BufferedImage actual, int diffSizeTrigger) {
    this.expected = expected;
    this.actual = actual;
    this.diff = new ImageDiffer().makeDiff(expected, actual);
    this.hasDiff = diff.withDiffSizeTrigger(diffSizeTrigger).hasDiff();
  }

  @Override
  public boolean getAsBoolean() {
    if(hasDiff) {
      ScreenshotTestExtension.setExpected(expected);
      ScreenshotTestExtension.setActual(actual);
      ScreenshotTestExtension.setDiff(diff.getMarkedImage());
    }
    return hasDiff;
  }
}
