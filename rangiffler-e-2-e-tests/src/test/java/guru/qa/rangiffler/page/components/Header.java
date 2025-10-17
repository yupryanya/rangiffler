package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.auth.WelcomePage;
import guru.qa.rangiffler.page.map.MapPage;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;

public class Header extends BaseComponent<Header> {
  private static final String HEADER_TITLE = "Rangiffler";
  private final String expandMenuIcon = "svg[data-testid='MenuIcon']";
  private final String mainPageLink = "a[href*='/my-travels']";
  private final String signOutIcon = "svg[data-testid='ExitToAppOutlinedIcon']";

  public Header(SelenideElement self) {
    super(self);
  }

  public Header() {
    super($("#root header"));
  }

  @Step("Verify header title is displayed")
  public void verifyHeaderTitle() {
    self.$("h1").shouldHave(text(HEADER_TITLE));
  }

  @Step("Sign out")
  public WelcomePage signOut() {
    self.$(signOutIcon).click();
    return new WelcomePage();
  }

  @Step("Navigate to Main page")
  public MapPage toMainPage() {
    self.$(mainPageLink).click();
    return new MapPage();
  }
}