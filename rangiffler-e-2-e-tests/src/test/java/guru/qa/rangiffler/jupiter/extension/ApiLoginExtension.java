package guru.qa.rangiffler.jupiter.extension;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.defs.FriendStatus;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.map.MapPage;
import guru.qa.rangiffler.service.api.AuthApiClient;
import guru.qa.rangiffler.service.grpc.UserService;
import io.qameta.allure.Step;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;
import org.openqa.selenium.Cookie;

import static guru.qa.rangiffler.jupiter.extension.UserExtension.getContextUser;
import static guru.qa.rangiffler.jupiter.extension.UserExtension.setContextUser;

public class ApiLoginExtension implements BeforeEachCallback, ParameterResolver {
  private final static Config CFG = Config.getInstance();

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(ApiLoginExtension.class);
  private final AuthApiClient authClient = new AuthApiClient();
  private final UserService userClient = new UserService();

  private final boolean setupBrowser;

  private ApiLoginExtension(boolean setupBrowser) {
    this.setupBrowser = setupBrowser;
  }

  public ApiLoginExtension() {
    this.setupBrowser = true;
  }

  public static ApiLoginExtension rest() {
    return new ApiLoginExtension(false);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), ApiLogin.class)
        .ifPresent(apiLogin -> {
          final UserJson contextUser = getContextUser();
          final boolean hasApiLoginCredentials = !"".equals(apiLogin.username()) && !"".equals(apiLogin.password());

          final String username;
          final String password;

          if (contextUser != null) {
            if (!hasApiLoginCredentials) {
              username = contextUser.username();
              password = contextUser.testData().password();
            } else {
              throw new IllegalArgumentException("Both context user and @ApiLogin credentials are set, choose one");
            }
          } else {
            if (hasApiLoginCredentials) {
              username = apiLogin.username();
              password = apiLogin.password();
              final UserJson user = userClient.findUserByUsername(username)
                  .orElseThrow(() -> new RuntimeException("User not found: " + username));
              final TestData testData = getTestData(username, password);
              setContextUser(user.withTestData(testData));
            } else {
              throw new IllegalArgumentException("User is not set. Use username/password in @ApiLogin.");
            }
          }

          try {
            String token = authClient.login(username, password);
            setToken(token);
            if (setupBrowser) setupBrowserSession();
          } catch (RuntimeException e) {
            throw new RuntimeException("Failed to login user: " + username, e);
          }
        });
  }

  private TestData getTestData(String username, String password) {
    return TestData.emptyTestData()
        .withPassword(password)
        .withFriends(userClient.getRelated(username, FriendStatus.FRIEND))
        .withIncomingRequests(userClient.getRelated(username, FriendStatus.INVITATION_RECEIVED))
        .withOutgoingRequests(userClient.getRelated(username, FriendStatus.INVITATION_SENT));
  }

  @Step("Setup browser session with token")
  private static void setupBrowserSession() {
    Selenide.open(CFG.frontUrl() + "favicon.ico");
    Selenide.localStorage().setItem("id_token", getToken());
    WebDriverRunner.getWebDriver().manage().addCookie(
        new Cookie("JSESSIONID", ThreadSafeCookieStore.INSTANCE.getCookieValue("JSESSIONID"))
    );
    Selenide.open(CFG.frontUrl(), MapPage.class)
        .verifyMainPageIsOpened();
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(String.class)
           && AnnotationSupport.isAnnotated(parameterContext.getParameter(), Token.class);
  }

  @Override
  public String resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return "Bearer " + getToken();
  }

  public static void setToken(String token) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("token", token);
  }

  public static String getToken() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("token", String.class);
  }

  public static void setCode(String code) {
    TestMethodContextExtension.context().getStore(NAMESPACE).put("code", code);
  }

  public static String getCode() {
    return TestMethodContextExtension.context().getStore(NAMESPACE).get("code", String.class);
  }

  public static Cookie getJSessionIdCookie() {
    return new Cookie("JSESSIONID", ThreadSafeCookieStore.INSTANCE.getCookieValue("JSESSIONID"));
  }
}