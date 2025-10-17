package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;

public class PhotoCard extends BaseComponent<PhotoCard> {

  public PhotoCard(SelenideElement self) {
    super(self);
  }

  public String getDescription() {
    return self.getText();
  }

  @Step("Click like icon on photo card")
  public PhotoCard like() {
    self.$("button[aria-label=like] svg").click();
    return this;
  }

  @Step("Click edit button on photo card")
  public PhotoModal edit() {
    self.$(byText("Edit")).click();
    return new PhotoModal();
  }

  @Step("Verify that photo card is liked")
  public PhotoCard shouldBeLiked() {
    self.$("button[aria-label=like] svg[data-testid='FavoriteOutlinedIcon']")
        .shouldBe(Condition.visible);
    return this;
  }
}