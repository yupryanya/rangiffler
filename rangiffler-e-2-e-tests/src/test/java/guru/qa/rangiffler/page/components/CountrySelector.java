package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.defs.Country;

import static com.codeborne.selenide.Selenide.$$;

public class CountrySelector extends BaseComponent<CountrySelector> {

  public CountrySelector(SelenideElement self) {
    super(self);
  }

  public CountrySelector selectCountry(Country country) {
    self.click();
    $$("li[role='option']")
        .findBy(Condition.text(country.getFullName()))
        .click();
    return this;
  }

  public void verifyCountrySelected(String value) {
    self.shouldHave(Condition.text(value));
  }
}