package guru.qa.rangiffler.page.auth;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.attribute;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.title;
import static guru.qa.rangiffler.defs.messages.ApplicationWarnings.SignupWarnings.VALIDATION_MESSAGE;

public class SignUpPage extends BasePage<SignUpPage> {
  public static final String SIGN_UP_TITLE_TEXT = "Register to Rangiffler";

  private final SelenideElement signInLink = $("a[class=form__link]");
  private final SelenideElement usernameInput = $("#username");
  private final SelenideElement usernameWarning = $("#username + .form__error");
  private final SelenideElement passwordInput = $("#password");
  private final SelenideElement passwordEyeIcon = $("#passwordBtn");
  private final SelenideElement passwordWarning = $("#password").closest("label").$(".form__error");
  private final SelenideElement passwordSubmitInput = $("#passwordSubmit");
  private final SelenideElement passwordSubmitEyeIcon = $("#passwordSubmitBtn");
  private final SelenideElement passwordSubmitWarning = $("#passwordSubmit").closest("label").$(".form__error");
  private final SelenideElement confirmSignUpButton = $("button[type='submit']");

  private String getUrl() {
    return CFG.authUrl() + "register";
  }

  @Step("Open 'Sign Up' page")
  public SignUpPage open() {
    return Selenide.open(getUrl(), SignUpPage.class);
  }

  @Step("Set username input field")
  public SignUpPage setUsername(String username) {
    usernameInput.setValue(username);
    return this;
  }

  @Step("Set password input field")
  public SignUpPage setPassword(String password) {
    passwordInput.setValue(password);
    return this;
  }

  @Step("Set password submit input field")
  public SignUpPage setPasswordSubmit(String passwordSubmit) {
    passwordSubmitInput.setValue(passwordSubmit);
    return this;
  }

  @Step("Click on submit button")
  public void clickSubmitButton() {
    confirmSignUpButton.click();
  }

  @Step("Signup with username, password and password confirmation")
  public SignUpPage doSignUp(String username, String password, String passwordSubmit) {
    setUsername(username);
    setPassword(password);
    setPasswordSubmit(passwordSubmit);
    clickSubmitButton();
    return this;
  }

  @Step("Signup with username and password")
  public SignUpPage doSignUp(String username, String password) {
    doSignUp(username, password, password);
    return this;
  }

  @Step("Verify username warning displayed")
  public SignUpPage verifyUsernameWarning(String expectedText) {
    usernameWarning.shouldHave(text(expectedText));
    return this;
  }

  @Step("Verify password warning displayed")
  public SignUpPage verifyPasswordWarning(String expectedText) {
    passwordWarning.shouldHave(text(expectedText));
    return this;
  }

  @Step("Verify submit password warning displayed")
  public SignUpPage verifySubmitPasswordWarning(String expectedText) {
    passwordSubmitWarning.shouldHave(text(expectedText));
    return this;
  }

  @Step("Verify empty username warning displayed")
  public SignUpPage verifyFillUsernameMessage() {
    usernameInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify empty password warning displayed")
  public SignUpPage verifyFillPasswordMessage() {
    passwordInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify empty password submit warning displayed")
  public SignUpPage verifyFillPasswordSubmitMessage() {
    passwordSubmitInput.shouldHave(attribute("validationMessage", VALIDATION_MESSAGE));
    return this;
  }

  @Step("Verify 'Sign Up' page title is displayed")
  public SignUpPage verifySignupPageIsOpened() {
    webdriver().shouldHave(title(SIGN_UP_TITLE_TEXT));
    return this;
  }

  @Step("Navigate to 'Login' page")
  public LoginPage navigateToLoginPage() {
    signInLink.click();
    return new LoginPage();
  }

  @Step("Verify password is displayed")
  public void verifyPasswordIsDisplayed(String password) {
    passwordEyeIcon.click();
    passwordInput.shouldHave(attribute("value", password));
  }

  @Step("Verify submit password is displayed")
  public void verifySubmitPasswordIsDisplayed(String password) {
    passwordSubmitEyeIcon.click();
    passwordSubmitInput.shouldHave(attribute("value", password));
  }
}