package guru.qa.rangiffler.test.web.map;

import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.PhotoCardJson;
import guru.qa.rangiffler.page.map.MapPage;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.utils.ImageUtils.toBase64FromClasspath;
import static guru.qa.rangiffler.utils.RandomDataUtils.randomString;

@WebTest
public class PhotoCardTest {
  private final MapPage mapPage = new MapPage();

  @User
  @ApiLogin
  @Test
  void shouldUploadNewPhotoSuccessfully() {
    final String image = "img/photo2.jpg";
    final Country country = Country.FR;
    final String description = randomString(30);

    PhotoCardJson expected = new PhotoCardJson(toBase64FromClasspath(image), 0, country, description);

    mapPage
        .clickAddPhotoButton()
        .uploadNewPhoto(image, country, description)
        .verifyFeedContainsPhotoCards(expected);
  }

  @User(
      photos = {
          @Photo(src = "img/photo1.jpg", country = Country.ES, likes = 3, description = "Barcelona holiday")
      }
  )
  @ApiLogin
  @Test
  void shouldUpdatePhotoImageSuccessfully() {
    final String newImage = "img/photo2.jpg";
    mapPage
        .editPhotoByDescription("Barcelona holiday")
        .updatePhoto(newImage)
        .verifyFeedContainsPhotoCards(
            new PhotoCardJson(toBase64FromClasspath(newImage), 3, Country.ES, "Barcelona holiday")
        );
  }
}