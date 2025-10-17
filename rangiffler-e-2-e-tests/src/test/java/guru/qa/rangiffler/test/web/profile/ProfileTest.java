package guru.qa.rangiffler.test.web.profile;

import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.map.MapPage;
import org.junit.jupiter.api.Test;

@WebTest
public class ProfileTest {
  private final MapPage mapPage = new MapPage();

  @User
  @ApiLogin
  @Test
  void shouldDisplayUserNameAsDisabledOnProfilePage(UserJson user) {
    mapPage
        .getSideBar()
        .toProfilePage()
        .verifyUserNameIsDisplayed(user.username())
        .verifyUserNameIsDisabled();
  }

  @User
  @ApiLogin
  @Test
  void shouldAllowUserToEditFirstNameOnProfilePage() {
    mapPage
        .getSideBar()
        .toProfilePage()
        .setFirstName("NewUserName")
        .verifyNameIsDisplayed("NewUserName");
  }

  @User
  @ApiLogin
  @Test
  void shouldAllowUserToEditLastNameOnProfilePage() {
    mapPage
        .getSideBar()
        .toProfilePage()
        .setLastName("NewUserName")
        .verifyLastNameIsDisplayed("NewUserName");
  }

  @User
  @ApiLogin
  @Test
  void shouldAllowUserToEditLocationOnProfilePage() {
    mapPage
        .getSideBar()
        .toProfilePage()
        .setCountry(Country.AL)
        .verifyCountryIsDisplayed(Country.AL);
  }
}