package guru.qa.rangiffler.page;

import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.config.Config;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;

public class BasePage<T extends BasePage> {
  protected static final Config CFG = Config.getInstance();

  protected final SelenideElement alert = $("div[role='alert']");

  public T checkAlertMessage(String message) {
    alert.should(visible)
        .should(text(message));
    return (T) this;
  }
}
