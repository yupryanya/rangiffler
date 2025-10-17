package guru.qa.rangiffler.test.gateway;

import com.apollographql.apollo.api.ApolloResponse;
import com.apollographql.apollo.exception.ApolloHttpException;
import com.apollographql.java.client.ApolloCall;
import com.apollographql.java.rx2.Rx2Apollo;
import guru.qa.GetCountriesQuery;
import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.Token;
import guru.qa.rangiffler.jupiter.annotation.User;
import org.junit.jupiter.api.Test;

import static io.qameta.allure.Allure.step;
import static org.assertj.core.api.Assertions.assertThat;

public class CountriesGqlTest extends BaseGqlTest {

  @User
  @ApiLogin
  @Test
  void shouldReturnAllCountriesForAuthorizedUser(@Token String bearerToken) {
    ApolloCall<GetCountriesQuery.Data> apolloCall = step(
        "Prepare Apollo call with authorization header",
        () -> apolloClient.query(new GetCountriesQuery())
            .addHttpHeader("Authorization", bearerToken)
    );

    ApolloResponse<GetCountriesQuery.Data> response = step(
        "Execute Apollo call and get response",
        () -> Rx2Apollo.single(apolloCall).blockingGet()
    );

    step("Validate response data contains 238 countries", () -> {
      GetCountriesQuery.Data responseData = response.dataOrThrow();
      assertThat(responseData.countries.size()).isEqualTo(238);
    });
  }

  @Test
  void shouldReturnUnauthorizedWithoutToken() {
    ApolloCall<GetCountriesQuery.Data> apolloCall = step(
        "Prepare Apollo call without authorization header",
        () -> apolloClient.query(new GetCountriesQuery())
    );

    ApolloResponse<GetCountriesQuery.Data> response = step(
        "Execute Apollo call and get response",
        () -> Rx2Apollo.single(apolloCall).blockingGet()
    );

    step("Validate response is null and error message contains 'Unauthorized'", () -> {
      assertThat(response.data).isNull();
      assertThat(response.errors.getFirst().getMessage()).contains("Unauthorized");
    });
  }

  @User
  @ApiLogin
  @Test
  void shouldReturn401ForBrokenToken(@Token String bearerToken) {
    ApolloCall<GetCountriesQuery.Data> apolloCall = step(
        "Prepare Apollo call with broken token",
        () -> apolloClient.query(new GetCountriesQuery())
            .addHttpHeader("Authorization", bearerToken + "!")
    );

    ApolloResponse<GetCountriesQuery.Data> response = step(
        "Execute Apollo call and get exception",
        () -> Rx2Apollo.single(apolloCall).blockingGet()
    );

    step("Validate exception status code is 401", () -> {
      ApolloHttpException apolloException = (ApolloHttpException) response.exception;
      assertThat(apolloException.getStatusCode()).isEqualTo(401);
    });
  }
}