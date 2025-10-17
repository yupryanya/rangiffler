package guru.qa.rangiffler.test.gateway;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Optional;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.LikePhotoMutation;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.GqlTest;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.grpc.PhotoService;
import guru.qa.type.CountryInput;
import guru.qa.type.LikeInput;
import guru.qa.type.PhotoInput;
import org.junit.jupiter.api.Test;

import static guru.qa.rangiffler.utils.ImageUtils.toBase64FromClasspath;
import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

@GqlTest
public class LikeGqlTest extends BaseGqlTest {

  private final PhotoService photoService = new PhotoService();

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldLikeOtherUsersPhoto(@Token String bearerToken, UserJson user) {
    String currentUserId = step("Get current user ID", () -> user.id().toString());

    UserJson friend = step("Get a friend", () -> user.testData().friends().getFirst());
    String friendPhotoId = step("Add a photo for friend", () ->
        photoService.addPhoto(
            friend.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo2.jpg"), Country.IT, "Vacation in Italy")
        ).id()
    );

    LikePhotoMutation mutation = step("Prepare LikePhotoMutation for friend's photo", () ->
        new LikePhotoMutation(
            new PhotoInput(
                Optional.present(friendPhotoId),
                Optional.present(""),
                Optional.present(new CountryInput("")),
                Optional.present(""),
                Optional.present(new LikeInput(currentUserId))
            )
        ));

    ApolloResponse<LikePhotoMutation.Data> response = step("Execute mutation", () -> {
      ApolloCall<LikePhotoMutation.Data> call = apolloClient.mutation(mutation)
          .addHttpHeader("Authorization", bearerToken);
      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify liked photo ID", () ->
        assertThat(response.data.photo.id).isEqualTo(friendPhotoId));
  }

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldNotEditOtherUsersPhotoWhenLikeIt(@Token String bearerToken, UserJson user) {
    UserJson friend = step("Get a friend", () -> user.testData().friends().getFirst());
    String friendPhotoId = step("Add a photo for friend", () ->
        photoService.addPhoto(
            friend.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo2.jpg"), Country.IT, "Vacation in Italy")
        ).id()
    );

    PhotoInput editInput = step("Prepare mutation attempting to edit friend's photo while liking it", () ->
        new PhotoInput(
            Optional.present(friendPhotoId),
            Optional.present(""),
            Optional.present(new CountryInput("")),
            Optional.present("New description"),
            Optional.present(new LikeInput(user.id().toString()))
        ));

    ApolloResponse<LikePhotoMutation.Data> response = step("Execute mutation", () -> {
      ApolloCall<LikePhotoMutation.Data> call = apolloClient.mutation(new LikePhotoMutation(editInput))
          .addHttpHeader("Authorization", bearerToken);
      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify photo data remains unchanged", () -> {
      assertThat(response.data.photo.id).isEqualTo(friendPhotoId);
      assertThat(response.data.photo.description).isEqualTo("Vacation in Italy");
    });
  }

  @Test
  @User
  @ApiLogin
  void shouldNotLikeOwnPhoto(@Token String bearerToken, UserJson user) {
    String currentUserId = step("Get current user ID", () -> user.id().toString());

    String ownPhotoId = step("Add a photo for current user", () ->
        photoService.addPhoto(
            user.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo1.jpg"), Country.US, "My selfie")
        ).id()
    );

    LikePhotoMutation mutation = step("Prepare mutation to like own photo", () ->
        new LikePhotoMutation(
            new PhotoInput(
                Optional.present(ownPhotoId),
                Optional.present(""),
                Optional.present(new CountryInput("")),
                Optional.present(""),
                Optional.present(new LikeInput(currentUserId))
            )
        ));

    ApolloResponse<LikePhotoMutation.Data> response = step("Execute mutation", () -> {
      ApolloCall<LikePhotoMutation.Data> call = apolloClient.mutation(mutation)
          .addHttpHeader("Authorization", bearerToken);
      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify that response data is null", () ->
        assertThat(response.data).isNull());
  }

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldUnlikeOtherUsersPhoto(@Token String bearerToken, UserJson user) {
    String currentUserId = user.id().toString();
    UserJson friend = user.testData().friends().getFirst();

    PhotoJson friendPhoto = step("Add friend's photo", () ->
        photoService.addPhoto(
            friend.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo2.jpg"), Country.IT, "Vacation in Italy")
        ));

    step("Like friend's photo", () ->
        photoService.likePhoto(user.username(), friendPhoto)
    );

    ApolloResponse<LikePhotoMutation.Data> response = step("Execute unlike mutation", () -> {
      LikePhotoMutation mutation = new LikePhotoMutation(
          new PhotoInput(
              Optional.present(friendPhoto.id()),
              Optional.present(""),
              Optional.present(new CountryInput("")),
              Optional.present(""),
              Optional.present(new LikeInput(currentUserId))
          )
      );

      ApolloCall<LikePhotoMutation.Data> call = apolloClient.mutation(mutation)
          .addHttpHeader("Authorization", bearerToken);

      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify user is no longer in likes", () ->
        assertThat(response.data.photo.likes.likes)
            .noneMatch(l -> l.user.equals(currentUserId)));
  }
}