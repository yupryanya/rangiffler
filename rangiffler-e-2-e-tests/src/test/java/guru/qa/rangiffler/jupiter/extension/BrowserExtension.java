package guru.qa.rangiffler.jupiter.extension;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.WebDriverRunner;
import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.extension.*;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.io.ByteArrayInputStream;

public class BrowserExtension implements
    BeforeEachCallback,
    AfterEachCallback,
    TestExecutionExceptionHandler,
    LifecycleMethodExecutionExceptionHandler {

  static {
    String browser = System.getenv("REMOTE_BROWSER");
    if (browser == null || browser.isEmpty()) {
      browser = "chrome";
    }

    Configuration.browser = browser;
    Configuration.timeout = 8000;
    Configuration.pageLoadStrategy = "eager";

    if ("docker".equals(System.getProperty("test.env"))) {
      Configuration.remote = "http://selenoid:4444/wd/hub";

      if ("chrome".equals(browser)) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--no-sandbox");
        Configuration.browserVersion = "128.0";
        Configuration.browserCapabilities = options;
      } else if ("firefox".equals(browser)) {
        FirefoxOptions options = new FirefoxOptions();
        Configuration.browserVersion = "125.0";
        Configuration.browserCapabilities = options;
      }
    }
  }

  @Override
  @Step("Close browser")
  public void afterEach(ExtensionContext context) throws Exception {
    if (WebDriverRunner.hasWebDriverStarted()) {
      Selenide.closeWebDriver();
    }
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    SelenideLogger.addListener("Allure-selenide", new AllureSelenide()
        .savePageSource(false)
        .screenshots(false)
    );
  }

  @Override
  public void handleTestExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleBeforeEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  @Override
  public void handleAfterEachMethodExecutionException(ExtensionContext context, Throwable throwable) throws Throwable {
    doScreenshot();
    throw throwable;
  }

  private static void doScreenshot() {
    if (WebDriverRunner.hasWebDriverStarted()) {
      Allure.addAttachment(
          "Screen on fail",
          new ByteArrayInputStream(
              ((TakesScreenshot) WebDriverRunner.getWebDriver()).getScreenshotAs(OutputType.BYTES)
          )
      );
    }
  }
}