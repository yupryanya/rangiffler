package guru.qa.rangiffler.page.profile;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.page.BasePage;
import guru.qa.rangiffler.page.components.CountrySelector;
import guru.qa.rangiffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.value;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.sleep;
import static org.assertj.core.api.Assertions.assertThat;

public class ProfilePage extends BasePage<ProfilePage> {
  protected static final Config CFG = Config.getInstance();
  public static final String URL = CFG.frontUrl() + "profile";

  private final SelenideElement profilePicture = $("#image__input").parent().$("img");
  private final SelenideElement defaultIcon = $("svg[data-testid='PersonIcon']");
  private final SelenideElement uploadNewPictureButton = $("#image__input");
  private final SelenideElement firstNameInput = $("#firstname");
  private final SelenideElement lastNameInput = $("#surname");
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement saveChangesButton = $("button[type='submit']");

  private final CountrySelector countrySelector = new CountrySelector($("#location"));

  @Step("Set country to {country}")
  public ProfilePage setCountry(Country country) {
    countrySelector.selectCountry(country);
    this.saveChanges();
    return this;
  }

  @Step("Set profile picture from path {path}")
  public ProfilePage setProfilePicture(String path) {
    uploadNewPictureButton.uploadFromClasspath(path);
    this.saveChanges();
    return this;
  }

  @Step("Set user first name to {newUserName}")
  public ProfilePage setFirstName(String newName) {
    firstNameInput.setValue(newName);
    this.saveChanges();
    return this;
  }

  @Step("Set user last name to {newUserName}")
  public ProfilePage setLastName(String newName) {
    lastNameInput.setValue(newName);
    this.saveChanges();
    return this;
  }

  @Step("Save changes")
  public ProfilePage saveChanges() {
    saveChangesButton.click();
    Selenide.refresh();
    return this;
  }

  @Step("Verify profile picture is displayed")
  public ProfilePage verifyProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = null;
    try {
      sleep(2000);
      actualImage = ImageIO.read(profilePicture.screenshot());
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the profile picture image", e);
    }
    boolean hasDiff = new ScreenDiffResult(expectedImage, actualImage, 500).getAsBoolean();
    assertThat(hasDiff).isFalse();
    return this;
  }

  @Step("Verify default profile picture is displayed")
  public ProfilePage verifyDefaultProfilePictureIsDisplayed(BufferedImage expectedImage) {
    BufferedImage actualImage = null;
    try {
      sleep(2000);
      actualImage = ImageIO.read(defaultIcon.screenshot());
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    boolean hasDiff = new ScreenDiffResult(expectedImage, actualImage, 600).getAsBoolean();
    assertThat(hasDiff).isFalse();
    return this;
  }

  @Step("Verify user first name is updated to {newUserName}")
  public ProfilePage verifyNameIsDisplayed(String newUserName) {
    Selenide.refresh();
    firstNameInput.shouldHave(value(newUserName));
    return this;
  }

  @Step("Verify user last name is updated to {newUserName}")
  public ProfilePage verifyLastNameIsDisplayed(String newUserName) {
    lastNameInput.shouldHave(value(newUserName));
    return this;
  }

  @Step("Verify user name is disabled")
  public ProfilePage verifyUserNameIsDisabled() {
    usernameInput.shouldBe(disabled);
    return this;
  }

  @Step("Verify user name is displayed")
  public ProfilePage verifyUserNameIsDisplayed(String username) {
    usernameInput.shouldHave(value(username));
    return this;
  }

  @Step("Verify country is displayed as {country}")
  public ProfilePage verifyCountryIsDisplayed(Country country) {
    countrySelector.verifyCountrySelected(country.getFullName());
    return this;
  }
}