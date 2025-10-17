package guru.qa.rangiffler.test.web.map;

import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.ScreenshotTest;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.map.MapPage;
import guru.qa.rangiffler.service.grpc.PhotoService;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;
import java.util.List;

import static guru.qa.rangiffler.utils.ImageUtils.toBase64FromClasspath;

@WebTest
public class StatsTest {
  private final MapPage mapPage = new MapPage();
  private final PhotoService photoService = new PhotoService();

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.CN),
          @Photo(src = "img/photo2.jpg", country = Country.US),
          @Photo(src = "img/photo3.jpg", country = Country.CN),
          @Photo(src = "img/photo4.jpg", country = Country.FR)
      }
  )
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/stats/my-expected-stats.png")
  void shouldDisplayOnlyMyPhotosOnMap(BufferedImage expectedImage) {
    mapPage.verifyMapIsLoaded(expectedImage);
  }

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.CN),
          @Photo(src = "img/photo2.jpg", country = Country.US)
      },
      friends = 2
  )
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/stats/with-friends-expected-stats.png")
  void shouldDisplayMyAndFriendsPhotosOnMapWhenSwitchedToWithFriends(BufferedImage expectedImage, UserJson user) {
    List<UserJson> friends = user.testData().friends();

    String friendPhoto1 = "img/photo3.jpg";
    Country friendCountry1 = Country.CN;
    String friendDescription1 = "Vacation in China";
    photoService.addPhoto(
        friends.get(0).username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto1), friendCountry1, friendDescription1));

    String friendPhoto2 = "img/photo4.jpg";
    Country friendCountry2 = Country.DE;
    String friendDescription2 = "Trip to Germany";
    photoService.addPhoto(
        friends.get(1).username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto2), friendCountry2, friendDescription2));

    mapPage
        .switchFeedToWithFriends()
        .verifyMapIsLoaded(expectedImage);
  }
}