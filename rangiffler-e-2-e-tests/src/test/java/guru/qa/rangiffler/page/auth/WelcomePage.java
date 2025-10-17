package guru.qa.rangiffler.page.auth;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.page.BasePage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$$;

public class WelcomePage extends BasePage<WelcomePage> {

  private String getUrl() {
    return CFG.frontUrl();
  }

  @Step("Open 'Welcome' page")
  public WelcomePage open() {
    return Selenide.open(getUrl(), WelcomePage.class);
  }

  @Step("Click on Login button")
  public LoginPage clickLoginButton() {
    $$("button").findBy(text("LOGIN")).click();
    return new LoginPage();
  }

  @Step("Click on Register link")
  public SignUpPage clickRegisterLink() {
    $$("a").findBy(text("REGISTER")).click();
    return new SignUpPage();
  }
}
