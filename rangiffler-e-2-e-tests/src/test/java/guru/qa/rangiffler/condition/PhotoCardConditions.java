package guru.qa.rangiffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.model.PhotoCardJson;
import org.apache.commons.lang.ArrayUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

public class PhotoCardConditions {
  public static WebElementsCondition exactPhotoCardsInAnyOrder(PhotoCardJson... expectedBubbles) {
    if (ArrayUtils.isEmpty(expectedBubbles)) {
      throw new IllegalArgumentException("Expected bubbles cannot be empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedBubbles.length) {
          return CheckResult.rejected(
              String.format("Expected %d elements, but found %d", expectedBubbles.length, elements.size()),
              elements
          );
        }

        List<PhotoCardJson> actualBubbles = elements.stream()
            .map(PhotoCardConditions::extractCardValues)
            .collect(toList());

        List<PhotoCardJson> expected = Arrays.asList(expectedBubbles);
        boolean passed = actualBubbles.containsAll(expected) && expected.containsAll(actualBubbles);

        if (!passed) {
          return CheckResult.rejected(
              String.format("Unordered bubble mismatch (expected: %s, actual: %s)", expected, actualBubbles),
              actualBubbles
          );
        }

        return CheckResult.accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedBubbles);
      }
    };
  }

  public static WebElementsCondition photoCardsContainAll(PhotoCardJson... expectedCards) {
    if (ArrayUtils.isEmpty(expectedCards)) {
      throw new IllegalArgumentException("Expected bubbles cannot be empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        List<PhotoCardJson> actualCards = elements.stream()
            .map(PhotoCardConditions::extractCardValues)
            .collect(toList());

        List<PhotoCardJson> expected = Arrays.asList(expectedCards);
        boolean passed = actualCards.containsAll(expected);

        if (!passed) {
          return CheckResult.rejected(
              String.format("Missing expected cards. Expected: %s, Actual: %s", expected, actualCards),
              actualCards
          );
        }

        return CheckResult.accepted();
      }

      @Override
      public String toString() {
        return Arrays.toString(expectedCards);
      }
    };
  }

  private static PhotoCardJson extractCardValues(WebElement card) {
    return new PhotoCardJson(
        card.findElement(By.className("photo-card__image")).getDomAttribute("src"),
        Integer.parseInt(card.findElement(By.cssSelector("[data-testid='FavoriteOutlinedIcon'] + p"))
            .getText()
            .replaceAll("\\D+", "")),
        Country.fromName(card.findElement(By.cssSelector("h3.MuiTypography-root")).getText()),
        card.findElement(By.className("photo-card__content")).getText()
    );
  }
}
