package guru.qa.rangiffler.page.auth;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class SignUpSuccessPage extends BasePage<SignUpSuccessPage> {
  private final SelenideElement successMessage = $(".form__paragraph.form__paragraph_success");
  private final SelenideElement LoginButton = $("a[class='form_sign-in']");

  public static final String SUCCESSFULLY_REGISTERED = "Congratulations! You've registered!";

  @Step("Click on Login button")
  public LoginPage clickLoginButton() {
    LoginButton.click();
    return new LoginPage();
  }

  @Step("Verify success sign up message")
  public SignUpPage verifySuccessSignUpMessage() {
    successMessage.shouldHave(text(SUCCESSFULLY_REGISTERED));
    return new SignUpPage();
  }
}
