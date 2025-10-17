package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.page.map.MapPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selenide.$;

public class PhotoModal {
  private final SelenideElement uploadImageInput = $("#image__input");
  private final SelenideElement photoDescriptionInput = $("#description");
  private final SelenideElement submitButton = $("button[type='submit']");

  private final CountrySelector countrySelector = new CountrySelector($("#country"));

  @Step("Select country {country}")
  public PhotoModal selectCountry(Country country) {
    countrySelector.selectCountry(country);
    return this;
  }

  @Step("Upload new photo from path {path} with country {country} and description {description}")
  public MapPage uploadNewPhoto(String path, Country country, String description) {
    uploadImageInput.uploadFromClasspath(path);
    selectCountry(country);
    photoDescriptionInput.setValue(description);
    submitButton.click();
    return new MapPage();
  }

  @Step("Update existing photo from path {path}")
  public MapPage updatePhoto(String path) {
    uploadImageInput.uploadFromClasspath(path);
    submitButton.click();
    return new MapPage();
  }
}
