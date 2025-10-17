package guru.qa.rangiffler.service.api;

import com.fasterxml.jackson.databind.JsonNode;
import guru.qa.rangiffler.api.AuthApi;
import guru.qa.rangiffler.api.core.CodeInterceptor;
import guru.qa.rangiffler.api.core.RestClient;
import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import guru.qa.rangiffler.utils.OAuthUtils;
import io.qameta.allure.Step;
import retrofit2.Response;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;

import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;

public class AuthApiClient extends RestClient {
  private static final Config CFG = Config.getInstance();
  public AuthApi authApi;

  public AuthApiClient() {
    super(CFG.authUrl(), true, JacksonConverterFactory.create(), BODY, new CodeInterceptor());
    this.authApi = retrofit.create(AuthApi.class);
  }

  @Step("Login user with API")
  public @Nonnull String login(@Nonnull String username, @Nonnull String password) {
    final String redirectUrl = CFG.frontUrl() + "authorized";
    final String clientId = "client";
    final String codeVerifier = OAuthUtils.generateCodeVerifier();
    final String codeChallenge = OAuthUtils.generateCodeChallenge(codeVerifier);

    try {
      authApi
          .preRequest("code", clientId, "openid", redirectUrl, codeChallenge, "S256")
          .execute();

      String xsrfToken = ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN");
      if (xsrfToken == null || xsrfToken.isEmpty()) {
        throw new RuntimeException("Missing XSRF token");
      }

      authApi
          .login(xsrfToken, username, password)
          .execute();

      String authCode = ApiLoginExtension.getCode();
      if (authCode == null || authCode.isEmpty()) {
        throw new RuntimeException("Missing authorization code");
      }

      Response<JsonNode> tokenResponse = authApi
          .token(authCode, redirectUrl, codeVerifier, "authorization_code", clientId)
          .execute();

      if (!tokenResponse.isSuccessful() || tokenResponse.body() == null
          || tokenResponse.body().get("access_token").asText() == null) {
        throw new RuntimeException("Missing access token");
      }

      return tokenResponse.body().get("access_token").asText();
    } catch (IOException e) {
      throw new RuntimeException("Failed to login");
    }
  }
}