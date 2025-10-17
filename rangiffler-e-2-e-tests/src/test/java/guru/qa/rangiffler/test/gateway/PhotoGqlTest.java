package guru.qa.rangiffler.test.gateway;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Optional;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.DeletePhotoMutation;
import guru.qa.LikePhotoMutation;
import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
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

public class PhotoGqlTest extends BaseGqlTest {

  private final PhotoService photoService = new PhotoService();

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldNotEditOtherUsersPhoto(@Token String bearerToken, UserJson user) {
    UserJson friend = step("Get a friend", () -> user.testData().friends().getFirst());

    String friendPhotoId = step("Add a photo for friend", () ->
        photoService.addPhoto(
            friend.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo2.jpg"), Country.IT, "Vacation in Italy")
        ).id()
    );

    PhotoInput editInput = step("Prepare mutation attempting to edit friend's photo", () ->
        new PhotoInput(
            Optional.present(friendPhotoId),
            Optional.present(""),
            Optional.present(new CountryInput("")),
            Optional.present("New description"),
            Optional.present(new LikeInput(""))
        )
    );

    ApolloResponse<LikePhotoMutation.Data> response = step("Execute mutation", () -> {
      ApolloCall<LikePhotoMutation.Data> call = apolloClient.mutation(new LikePhotoMutation(editInput))
          .addHttpHeader("Authorization", bearerToken);
      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify that response data is null", () ->
        assertThat(response.data).isNull());
  }

  @Test
  @User(friends = 1)
  @ApiLogin
  void shouldNotDeleteOtherUsersPhoto(@Token String bearerToken, UserJson user) {
    UserJson friend = step("Get a friend", () -> user.testData().friends().getFirst());

    String friendPhotoId = step("Add a photo for friend", () ->
        photoService.addPhoto(
            friend.username(),
            new PhotoJson(null, toBase64FromClasspath("img/photo2.jpg"), Country.IT, "Vacation in Italy")
        ).id()
    );

    DeletePhotoMutation mutation = step("Prepare delete mutation for friend's photo", () ->
        new DeletePhotoMutation(friendPhotoId)
    );

    ApolloResponse<DeletePhotoMutation.Data> response = step("Execute delete mutation", () -> {
      ApolloCall<DeletePhotoMutation.Data> call = apolloClient.mutation(mutation)
          .addHttpHeader("Authorization", bearerToken);
      return Rx2Apollo.single(call).blockingGet();
    });

    step("Verify unauthorized error", () -> {
      assertThat(response.data.deletePhoto).isNull();
      assertThat(response.errors.getFirst().getMessage()).contains("Unauthorized");
    });
  }
}