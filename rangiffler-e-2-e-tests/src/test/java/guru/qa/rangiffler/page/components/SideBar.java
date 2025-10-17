package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.page.map.MapPage;
import guru.qa.rangiffler.page.people.FriendsTabPage;
import guru.qa.rangiffler.page.profile.ProfilePage;
import io.qameta.allure.Step;

public class SideBar extends BaseComponent<SideBar> {
  private final String peopleIcon = "svg[data-testid='PersonSearchRoundedIcon']";
  private final String profileIcon = "svg[data-testid='AccountCircleRoundedIcon']";
  private final String mapIcon = "svg[data-testid='PublicRoundedIcon']";

  public SideBar(SelenideElement self) {
    super(self);
  }

  @Step("Navigate to People page")
  public FriendsTabPage toPeoplePage() {
    self.$(peopleIcon).click();
    return new FriendsTabPage();
  }

  @Step("Navigate to Profile page")
  public ProfilePage toProfilePage() {
    self.$(profileIcon).click();
    return new ProfilePage();
  }

  @Step("Navigate to map page")
  public MapPage toMapPage() {
    self.$(mapIcon).click();
    return new MapPage();
  }
}