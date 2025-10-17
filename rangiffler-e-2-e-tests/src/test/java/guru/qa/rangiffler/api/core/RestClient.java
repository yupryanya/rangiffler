package guru.qa.rangiffler.api.core;

import io.qameta.allure.okhttp3.AllureOkHttp3;
import lombok.Getter;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.apache.commons.lang.ArrayUtils;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.util.Objects;

import static okhttp3.logging.HttpLoggingInterceptor.Level;
import static okhttp3.logging.HttpLoggingInterceptor.Level.BODY;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static retrofit2.Converter.Factory;

public class RestClient {
  private final OkHttpClient okHttpClient;

  @Getter
  protected final Retrofit retrofit;

  public RestClient(String baseUrl) {
    this(baseUrl, false, JacksonConverterFactory.create(), BODY);
  }

  public RestClient(String baseUrl,
                    boolean followRedirect,
                    Factory converterFactory,
                    Level loggingLevel,
                    Interceptor... interceptors) {

    final OkHttpClient.Builder builder = new OkHttpClient.Builder()
        .followRedirects(followRedirect);

    if (ArrayUtils.isNotEmpty(interceptors)) {
      for (Interceptor interceptor : interceptors) {
        builder.addNetworkInterceptor(interceptor);
      }
    }
    builder.addNetworkInterceptor(new AllureOkHttp3()
        .setRequestTemplate("request.ftl")
        .setResponseTemplate("response.ftl")
    );
    builder.addNetworkInterceptor(new HttpLoggingInterceptor()
        .setLevel(loggingLevel));
    builder.cookieJar(new JavaNetCookieJar(
        new CookieManager(
            ThreadSafeCookieStore.INSTANCE,
            CookiePolicy.ACCEPT_ALL
        )
    ));

    this.okHttpClient = builder.build();
    this.retrofit = new Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(converterFactory)
        .build();
  }

  public @Nonnull <T> T execute(Call<T> call, int expectedStatusCode) {
    try {
      Response<T> response = call.execute();
      assertEquals(expectedStatusCode, response.code(), "Unexpected HTTP status code");
      return Objects.requireNonNull(response.body());
    } catch (IOException e) {
      throw new AssertionError("Failed to execute API request", e);
    }
  }
}