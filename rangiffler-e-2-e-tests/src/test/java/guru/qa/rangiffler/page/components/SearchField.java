package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.Keys;

public class SearchField extends BaseComponent<SearchField> {
  private final String searchInput = "input[aria-label='search people']";

  public SearchField(SelenideElement self) {
    super(self);
  }

  @Step("Enter search input: {description}")
  public SearchField enterSearchValue(String value) {
    self.$(searchInput).setValue(value).pressEnter();
    return this;
  }

  @Step("Clear search input")
  public SearchField clearSearchInput() {
    self.$(searchInput).sendKeys(Keys.chord(Keys.CONTROL, "a"), Keys.BACK_SPACE);
    return this;
  }

  @Step("Search for description: {description}")
  public SearchField search(String value) {
    clearSearchInput();
    enterSearchValue(value);
    return this;
  }
}