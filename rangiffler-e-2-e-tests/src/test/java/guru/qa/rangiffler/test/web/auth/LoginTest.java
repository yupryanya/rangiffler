package guru.qa.rangiffler.test.web.auth;

import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.auth.LoginPage;
import guru.qa.rangiffler.page.auth.WelcomePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;

import static guru.qa.rangiffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.rangiffler.utils.RandomDataUtils.nonExistentUserName;

@WebTest
public class LoginTest {
  private final WelcomePage welcomePage = new WelcomePage();
  private final LoginPage loginPage = new LoginPage();

  @Test
  void shouldNavigateToLoginPageFromWelcomePage() {
    welcomePage
        .open()
        .clickLoginButton()
        .verifyLoginPageIsOpened();
  }

  @User
  @Test
  void shouldLoginWithValidCredentials(UserJson user) {
    welcomePage.open()
        .clickLoginButton()
        .doLogin(user.username(), user.testData().password())
        .verifyMainPageIsOpened();
  }

  @User
  @Test
  void shouldNotLoginWithInvalidUsername(UserJson user) {
    welcomePage.open()
        .clickLoginButton()
        .doLogin(nonExistentUserName(), user.testData().password());
    loginPage
        .verifyLoginPageIsOpened()
        .verifyBadCredentialsErrorMessage();
  }

  @User
  @Test
  void shouldNotLoginWithInvalidPassword(UserJson user) {
    welcomePage.open()
        .clickLoginButton()
        .doLogin(user.username(), newValidPassword());
    loginPage
        .verifyLoginPageIsOpened()
        .verifyBadCredentialsErrorMessage();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyUsername(String username) {
    welcomePage.open()
        .clickLoginButton()
        .doLogin(username, newValidPassword());
    loginPage
        .verifyLoginPageIsOpened()
        .verifyFillUsernameMessage();
  }

  @User
  @ParameterizedTest
  @EmptySource
  void shouldNotLoginWithEmptyPassword(String password, UserJson user) {
    welcomePage.open()
        .clickLoginButton()
        .doLogin(user.username(), password);
    loginPage
        .verifyLoginPageIsOpened()
        .verifyFillPasswordMessage();
  }

  @Test
  void shouldNavigateToSignUpPageFromWelcomePage() {
    welcomePage.open()
        .clickLoginButton()
        .navigateToSignUpPage()
        .verifySignupPageIsOpened();
  }
}