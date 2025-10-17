package guru.qa.rangiffler.page.map;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.page.BasePage;
import guru.qa.rangiffler.page.components.Header;
import guru.qa.rangiffler.page.components.PhotoCard;
import guru.qa.rangiffler.page.components.PhotoModal;
import guru.qa.rangiffler.page.components.SideBar;
import guru.qa.rangiffler.utils.ScreenDiffResult;
import io.qameta.allure.Step;
import lombok.Getter;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;

import static com.codeborne.selenide.CollectionCondition.size;
import static com.codeborne.selenide.Condition.disabled;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.*;
import static guru.qa.rangiffler.condition.PhotoCardConditions.exactPhotoCardsInAnyOrder;
import static guru.qa.rangiffler.condition.PhotoCardConditions.photoCardsContainAll;
import static org.assertj.core.api.Assertions.assertThat;

public class MapPage extends BasePage<MapPage> {
  private final String MAP_PAGE_TITLE_TEXT = "Travels map";

  private final SelenideElement pageTitle = $("h2.MuiTypography-h4");
  private final SelenideElement myToggleButton = $("button[valye='my']");
  private final SelenideElement withFriendsToggleButton = $("button[value='friends']");
  private final SelenideElement addPhotoButton = $("button[aria-label='Add photo']");
  private final SelenideElement map = $("figure.worldmap__figure-container");
  private final ElementsCollection photoCards = $$("div.MuiGrid-container div.MuiGrid-item");

  @Getter
  private Header header = new Header();
  @Getter
  private SideBar sideBar = new SideBar($(".MuiDrawer-paperAnchorDockedLeft"));

  protected String getUrl() {
    return CFG.frontUrl() + "my-travels";
  }

  @Step("Open 'My travels' page")
  public MapPage open() {
    return Selenide.open(getUrl(), this.getClass());
  }

  @Step("Verify main page is opened")
  public MapPage verifyMainPageIsOpened() {
    pageTitle.shouldHave(text(MAP_PAGE_TITLE_TEXT));
    return this;
  }

  @Step("Click 'Add photo' button")
  public PhotoModal clickAddPhotoButton() {
    addPhotoButton.click();
    return new PhotoModal();
  }

  @Step("Switch feed to 'With friends'")
  public MapPage switchFeedToWithFriends() {
    withFriendsToggleButton.click();
    return this;
  }

  @Step("Switch feed to 'My travels'")
  public MapPage switchFeedToMyTravels() {
    myToggleButton.click();
    return this;
  }

  @Step("Verify feed contains {number} photos")
  public MapPage verifyFeedContainsNumberOfPhotos(int number) {
    photoCards.shouldHave(size(number));
    return this;
  }

  @Step("Verify feed contains expected photo cards")
  public void verifyFeedContainsPhotoCards(PhotoCardJson... cards) {
    photoCards.should(photoCardsContainAll(cards));
  }

  @Step("Verify feed displays exactly expected photo cards")
  public void verifyFeedDisplaysExactlyPhotoCards(PhotoCardJson... cards) {
    photoCards.should(exactPhotoCardsInAnyOrder(cards));
  }

  @Step("Edit photo by description '{description}'")
  public PhotoModal editPhotoByDescription(String description) {
    return findPhotoCardByDescription(description).edit();
  }

  @Step("Go to next page")
  public MapPage goToNextPage() {
    $$("button").findBy(text("Next")).click();
    return this;
  }

  @Step("Verify 'Next' button is disabled")
  public MapPage verifyNextButtonIsDisabled() {
    $$("button").findBy(text("Next")).shouldBe(disabled);
    return this;
  }

  @Step("Verify map is loaded correctly")
  public MapPage verifyMapIsLoaded(BufferedImage expectedImage) {
    try {
      sleep(3000);
      BufferedImage actualImage = ImageIO.read(map.screenshot());
      boolean hasDiff = new ScreenDiffResult(expectedImage, actualImage, 3000).getAsBoolean();
      assertThat(hasDiff).isFalse();
    } catch (IOException e) {
      throw new RuntimeException("Failed to read the map image", e);
    }
    return this;
  }

  @Step("Like photo by description '{description}'")
  public MapPage likePhotoByDescription(String description) {
    findPhotoCardByDescription(description).like();
    return this;
  }

  @Step("Find photo card by description '{description}'")
  private PhotoCard findPhotoCardByDescription(String description) {
    return new PhotoCard(photoCards.findBy(text(description)));
  }

  @Step("Verify photo with description '{description}' is liked")
  public MapPage verifyPhotoIsLiked(String description) {
    findPhotoCardByDescription(description).shouldBeLiked();
    return this;
  }
}
