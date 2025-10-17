package guru.qa.rangiffler.test.web.auth;

import com.codeborne.selenide.Selenide;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.auth.SignUpPage;
import guru.qa.rangiffler.page.auth.SignUpSuccessPage;
import guru.qa.rangiffler.page.auth.WelcomePage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static guru.qa.rangiffler.defs.messages.ApplicationWarnings.SignupWarnings.*;
import static guru.qa.rangiffler.utils.RandomDataUtils.*;

@WebTest
public class SignUpTest {
  private static final Config CFG = Config.getInstance();
  private final SignUpPage signUpPage = new SignUpPage();

  @Test
  void shouldRegisterNewUser() {
    Selenide.open(CFG.authUrl(), WelcomePage.class)
        .clickRegisterLink()
        .doSignUp(nonExistentUserName(), newValidPassword());
    new SignUpSuccessPage()
        .verifySuccessSignUpMessage();
  }

  @User
  @Test
  void shouldNotRegisterUserWithExistingUsername(UserJson user) {
    signUpPage.open()
        .doSignUp(user.username(), newValidPassword())
        .verifyUsernameWarning(String.format(USER_EXISTS, user.username()))
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyUsername(String username) {
    signUpPage.open()
        .doSignUp(username, newValidPassword())
        .verifyFillUsernameMessage()
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyPassword(String password) {
    signUpPage.open()
        .doSignUp(nonExistentUserName(), password, newValidPassword())
        .verifyFillPasswordMessage()
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @EmptySource
  void shouldNotRegisterWithEmptyConfirmationPassword(String passwordSubmit) {
    signUpPage.open()
        .doSignUp(nonExistentUserName(), newValidPassword(), passwordSubmit)
        .verifyFillPasswordSubmitMessage()
        .verifySignupPageIsOpened();
  }

  @Test
  void shouldNotRegisterIfPasswordAndConfirmationPasswordNotEqual() {
    signUpPage.open()
        .doSignUp(nonExistentUserName(), newValidPassword(), newValidPassword())
        .verifyPasswordWarning(PASSWORDS_DO_NOT_MATCH)
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @MethodSource("invalidUsernames")
  void shouldNotRegisterWithInvalidUsername(String username) {
    signUpPage.open()
        .doSignUp(username, newValidPassword())
        .verifyUsernameWarning(INVALID_USERNAME)
        .verifySignupPageIsOpened();
  }

  @ParameterizedTest
  @MethodSource("invalidPasswords")
  void shouldNotRegisterWithInvalidPassword(String password) {
    signUpPage.open()
        .doSignUp(nonExistentUserName(), password)
        .verifyPasswordWarning(INVALID_PASSWORD)
        .verifySubmitPasswordWarning(INVALID_PASSWORD)
        .verifySignupPageIsOpened();
  }

  @Test
  void shouldNavigateToLoginPage() {
    signUpPage.open()
        .navigateToLoginPage()
        .verifyLoginPageIsOpened();
  }

  @Test
  void shouldDisplayPassword() {
    String password = newValidPassword();
    signUpPage.open()
        .setPassword(password)
        .verifyPasswordIsDisplayed(password);
  }

  @Test
  void shouldDisplaySubmitPassword() {
    String password = newValidPassword();
    signUpPage.open()
        .setPasswordSubmit(password)
        .verifySubmitPasswordIsDisplayed(password);
  }

  private static Stream<Arguments> invalidUsernames() {
    return Stream.of(
        Arguments.of(shortUsername()),
        Arguments.of(longUsername())
    );
  }

  private static Stream<Arguments> invalidPasswords() {
    return Stream.of(
        Arguments.of(shortPassword()),
        Arguments.of(longPassword())
    );
  }
}
