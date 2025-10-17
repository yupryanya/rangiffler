package guru.qa.rangiffler.test.gateway;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.api.Optional;
import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.UpdateUserMutation;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.type.CountryInput;
import guru.qa.type.UserInput;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class UserGqlTest extends BaseGqlTest {

  @Test
  @User
  @ApiLogin
  void shouldUpdateUserSuccessfully(@Token String bearerToken, UserJson user) {
    UserInput input = step("Create user input with boundary values", () ->
        new UserInput(
            Optional.present("A".repeat(50)),
            Optional.present("B".repeat(100)),
            Optional.present(""),
            Optional.present(new CountryInput("fr"))
        )
    );

    ApolloCall<UpdateUserMutation.Data> call = step("Prepare update mutation", () ->
        apolloClient.mutation(new UpdateUserMutation(input))
            .addHttpHeader("Authorization", bearerToken)
    );

    ApolloResponse<UpdateUserMutation.Data> response = step("Execute mutation", () ->
        Rx2Apollo.single(call).blockingGet()
    );

    step("Verify user details are updated correctly", () -> {
      assertThat(response.data.user.id).isEqualTo(user.id().toString());
      assertThat(response.data.user.username).isEqualTo(user.username());
      assertThat(response.data.user.firstname).isEqualTo(input.firstname.getOrNull());
      assertThat(response.data.user.surname).isEqualTo(input.surname.getOrNull());
      assertThat(response.data.user.avatar).isEqualTo(input.avatar.getOrNull());
      assertThat(response.data.user.location.code).isEqualTo(input.location.getOrNull().code);
    });
  }

  @Test
  void shouldReturnUnauthorizedWithoutToken() {
    UserInput input = step("Create user input with boundary values", () ->
        new UserInput(
            Optional.present("A".repeat(50)),
            Optional.present("B".repeat(100)),
            Optional.present(""),
            Optional.present(new CountryInput("fr"))
        )
    );

    ApolloCall<UpdateUserMutation.Data> call = step("Prepare mutation without token", () ->
        apolloClient.mutation(new UpdateUserMutation(input))
    );

    ApolloResponse<UpdateUserMutation.Data> response = step("Execute mutation", () ->
        Rx2Apollo.single(call).blockingGet()
    );

    step("Verify unauthorized response", () -> {
      assertThat(response.data).isNull();
      assertThat(response.errors.getFirst().getMessage()).contains("Unauthorized");
    });
  }

  @Test
  @User
  @ApiLogin
  void shouldReturn401ForBrokenToken(@Token String bearerToken) {
    UserInput input = step("Create user input with boundary values", () ->
        new UserInput(
            Optional.present("A".repeat(50)),
            Optional.present("B".repeat(100)),
            Optional.present(""),
            Optional.present(new CountryInput("fr"))
        )
    );

    ApolloCall<UpdateUserMutation.Data> apolloCall = step("Prepare mutation with broken token", () ->
        apolloClient.mutation(new UpdateUserMutation(input))
            .addHttpHeader("Authorization", bearerToken + "!")
    );

    ApolloResponse<UpdateUserMutation.Data> response = step("Execute mutation", () ->
        Rx2Apollo.single(apolloCall).blockingGet()
    );

    step("Verify 401 error", () -> {
      ApolloHttpException apolloException = (ApolloHttpException) response.exception;
      assertThat(apolloException.getStatusCode()).isEqualTo(401);
    });
  }
}