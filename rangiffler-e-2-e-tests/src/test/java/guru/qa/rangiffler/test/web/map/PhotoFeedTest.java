package guru.qa.rangiffler.test.web.map;

import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.map.MapPage;
import guru.qa.rangiffler.service.grpc.PhotoService;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.rangiffler.utils.ImageUtils.toBase64FromClasspath;

@WebTest
public class PhotoFeedTest {
  private final MapPage mapPage = new MapPage();
  private final PhotoService photoService = new PhotoService();

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, description = "Barcelona holiday")
      },
      friends = 2
  )
  @ApiLogin
  @Test
  void shouldDisplayFriendsPhotosInFeedWhenSwitchedToWithFriends(UserJson user) {
    List<UserJson> friends = user.testData().friends();

    String friendPhoto1 = "img/photo2.jpg";
    Country friendCountry1 = Country.IT;
    String friendDescription1 = "Vacation in Italy";
    photoService.addPhoto(
        friends.get(0).username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto1), friendCountry1, friendDescription1)
    );

    String friendPhoto2 = "img/photo3.jpg";
    Country friendCountry2 = Country.DE;
    String friendDescription2 = "Trip to Germany";
    photoService.addPhoto(
        friends.get(1).username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto2), friendCountry2, friendDescription2)
    );

    mapPage
        .switchFeedToWithFriends()
        .verifyFeedContainsPhotoCards(
            new PhotoCardJson(toBase64FromClasspath("img/photo1.jpg"), 0, Country.ES, "Barcelona holiday"),
            new PhotoCardJson(toBase64FromClasspath(friendPhoto1), 0, friendCountry1, friendDescription1),
            new PhotoCardJson(toBase64FromClasspath(friendPhoto2), 0, friendCountry2, friendDescription2)
        );
  }

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, likes = 2, description = "Barcelona holiday"),
          @Photo(src = "img/photo2.jpg", country = Country.FR, likes = 1, description = "Vacation in France")
      },
      friends = 1
  )
  @ApiLogin
  @Test
  void shouldDisplayOnlyMyPhotosInFeed(UserJson user) {
    UserJson friend = user.testData().friends().getFirst();

    photoService.addPhoto(
        friend.username(),
        new PhotoJson(null, toBase64FromClasspath("img/photo3.jpg"), Country.AE, "Trip to Dubai")
    );

    mapPage.open()
        .verifyFeedDisplaysExactlyPhotoCards(
            new PhotoCardJson(toBase64FromClasspath("img/photo1.jpg"), 2, Country.ES, "Barcelona holiday"),
            new PhotoCardJson(toBase64FromClasspath("img/photo2.jpg"), 1, Country.FR, "Vacation in France"));
  }

  @User(randomPhoto = 15)
  @ApiLogin
  @Test
  void shouldDisplayCorrectNumberOfPhotosWhenPaginationApplied() {
    mapPage
        .verifyFeedContainsNumberOfPhotos(12)
        .goToNextPage()
        .verifyFeedContainsNumberOfPhotos(3)
        .verifyNextButtonIsDisabled();
  }

  @User(randomPhoto = 12)
  @ApiLogin
  @Test
  void shouldDisableNextButtonWhenNoMorePhotos() {
    mapPage
        .verifyFeedContainsNumberOfPhotos(12)
        .verifyNextButtonIsDisabled();
  }
}