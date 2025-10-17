package guru.qa.rangiffler.test.web.profile;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.ScreenshotTest;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.page.map.MapPage;
import org.junit.jupiter.api.Test;

import java.awt.image.BufferedImage;

@WebTest
public class ProfilePictureTest {
  private final MapPage mapPage = new MapPage();

  @User
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/profile/default-profile-picture.png")
  void shouldDisplayDefaultProfilePictureForNewUser(BufferedImage expectedImage) {
    mapPage
        .getSideBar()
        .toProfilePage()
        .verifyDefaultProfilePictureIsDisplayed(expectedImage);
  }

  @User
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/profile/profile-picture-150x150-resized.jpg")
  void shouldResizeProfilePictureUpAfterUpload(BufferedImage expectedImage) {
    mapPage
        .getSideBar()
        .toProfilePage()
        .setProfilePicture("img/profile/profile-picture-150x150.jpg")
        .verifyProfilePictureIsDisplayed(expectedImage);
  }

  @User
  @ApiLogin
  @Test
  @ScreenshotTest(value = "img/profile/profile-picture-500x500-resized.png")
  void shouldResizeProfilePictureDownAfterUpload(BufferedImage expectedImage) {
    mapPage
        .getSideBar()
        .toProfilePage()
        .setProfilePicture("img/profile/profile-picture-500x500.png")
        .verifyProfilePictureIsDisplayed(expectedImage);
  }
}
