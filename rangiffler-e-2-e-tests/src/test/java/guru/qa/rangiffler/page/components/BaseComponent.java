package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.SelenideElement;

public class BaseComponent<T extends BaseComponent<?>> {

  protected final SelenideElement self;

  public BaseComponent(SelenideElement self) {
    this.self = self;
  }
}