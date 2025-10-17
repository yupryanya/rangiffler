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

import static guru.qa.rangiffler.utils.ImageUtils.toBase64FromClasspath;

@WebTest
public class LikeTest {

  private final PhotoService photoService = new PhotoService();
  private final MapPage mapPage = new MapPage();

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, description = "Barcelona holiday")
      },
      friends = 1
  )
  @ApiLogin
  @Test
  void shouldLikeOtherUsersPhoto(UserJson user) {
    UserJson friend = user.testData().friends().getFirst();

    String friendPhoto = "img/photo2.jpg";
    Country friendCountry = Country.IT;
    String friendDescription = "Vacation in Italy";
    photoService.addPhoto(
        friend.username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto), friendCountry, friendDescription)
    );

    mapPage.open()
        .switchFeedToWithFriends()
        .likePhotoByDescription(friendDescription)
        .verifyPhotoIsLiked(friendDescription)
        .verifyFeedContainsPhotoCards(
            new PhotoCardJson(toBase64FromClasspath(friendPhoto), 1, friendCountry, friendDescription)
        );
  }

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, likes = 3, description = "Barcelona holiday")
      }
  )
  @ApiLogin
  @Test
  void shouldNotLikeOwnPhoto() {
    mapPage.open()
        .likePhotoByDescription("Barcelona holiday")
        .verifyFeedContainsPhotoCards(
            new PhotoCardJson(toBase64FromClasspath("img/photo1.jpg"), 3, Country.ES, "Barcelona holiday")
        );
  }

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, likes = 3, description = "Barcelona holiday")
      },
      friends = 1
  )
  @ApiLogin
  @Test
  void shouldDecreaseLikesCountWhenPhotoIsUnliked(UserJson user) {
    UserJson friend = user.testData().friends().getFirst();

    String friendPhoto = "img/photo2.jpg";
    Country friendCountry = Country.IT;
    String friendDescription = "Vacation in Italy";
    PhotoJson friendsPhoto = photoService.addPhoto(
        friend.username(),
        new PhotoJson(null, toBase64FromClasspath(friendPhoto), friendCountry, friendDescription)
    );

    photoService.likePhoto(user.username(), friendsPhoto);

    mapPage.open()
        .switchFeedToWithFriends()
        .likePhotoByDescription(friendDescription)
        .verifyFeedContainsPhotoCards(
            new PhotoCardJson(toBase64FromClasspath(friendPhoto), 0, friendCountry, friendDescription)
        );
  }
}