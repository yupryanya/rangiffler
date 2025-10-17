package guru.qa.rangiffler.condition;

import com.codeborne.selenide.CheckResult;
import com.codeborne.selenide.Driver;
import com.codeborne.selenide.WebElementsCondition;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.model.UserJson;
import org.apache.commons.lang3.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

@ParametersAreNonnullByDefault
public class FriendsTableConditions {
  public static @Nonnull WebElementsCondition tableHasUsersAnyOrder(UserJson... expectedUsers) {
    if (ArrayUtils.isEmpty(expectedUsers)) {
      throw new IllegalArgumentException("Expected users cannot be null or empty");
    }

    return new WebElementsCondition() {
      @Override
      public CheckResult check(Driver driver, List<WebElement> elements) {
        if (elements.size() != expectedUsers.length) {
          return CheckResult.rejected(
              String.format("Expected %d rows, but found %d", expectedUsers.length, elements.size()),
              elements
          );
        }

        List<List<String>> actualRows = elements.stream()
            .map(this::extractCellTexts)
            .sorted(Comparator.comparing(a -> a.get(0)))
            .toList();

        List<List<String>> expectedRows = Arrays.stream(expectedUsers)
            .map(this::convertToExpectedList)
            .sorted(Comparator.comparing(a -> a.get(0)))
            .toList();

        List<String> allMismatches = new ArrayList<>();

        for (int i = 0; i < actualRows.size(); i++) {
          List<String> actual = actualRows.get(i);
          List<String> expected = expectedRows.get(i);

          compare("username", i, expected.get(0), actual.get(0), allMismatches);
          compare("name", i, expected.get(1), actual.get(1), allMismatches);
          compare("surname", i, expected.get(2), actual.get(2), allMismatches);
          compare("location", i, expected.get(3), actual.get(3), allMismatches);
        }

        if (!allMismatches.isEmpty()) {
          return CheckResult.rejected(String.join("\n", allMismatches), actualRows);
        }

        return CheckResult.accepted();
      }

      private void compare(String field, int row, String expected, String actual, List<String> errors) {
        if (!expected.equals(actual)) {
          errors.add(String.format("Row %d: %s mismatch (expected: %s, actual: %s)", row, field, expected, actual));
        }
      }

      private List<String> extractCellTexts(WebElement row) {
        return row.findElements(By.tagName("td"))
            .stream()
            .limit(4)
            .map(e -> e.getText().trim())
            .toList();
      }

      private List<String> convertToExpectedList(UserJson user) {
        return List.of(
            user.username(),
            user.firstname() == null ? "" : user.firstname(),
            user.surname() == null ? "" : user.surname(),
            Country.fromCode(user.countryCode()).getFullName()
        );
      }

      @NotNull
      @Override
      public String toString() {
        List<List<String>> expectedFormatted = Arrays.stream(expectedUsers)
            .map(this::convertToExpectedList)
            .toList();

        return expectedFormatted.toString();
      }
    };
  }
}