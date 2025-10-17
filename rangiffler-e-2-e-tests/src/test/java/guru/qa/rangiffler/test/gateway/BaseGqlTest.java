package guru.qa.rangiffler.test.gateway;

import com.apollographql.adapter.core.DateAdapter;
import com.apollographql.java.client.ApolloClient;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.jupiter.annotation.meta.GqlTest;
import guru.qa.rangiffler.jupiter.extension.ApiLoginExtension;
import guru.qa.type.Date;
import io.qameta.allure.okhttp3.AllureOkHttp3;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.extension.RegisterExtension;

@GqlTest
public class BaseGqlTest {
  protected static final Config CFG = Config.getInstance();

  @RegisterExtension
  protected static ApiLoginExtension apiLoginExtension = ApiLoginExtension.rest();

  protected static final ApolloClient apolloClient = new ApolloClient.Builder()
      .serverUrl(CFG.gatewayUrl() + "graphql")
      .addCustomScalarAdapter(Date.type, DateAdapter.INSTANCE)
      .okHttpClient(new OkHttpClient.Builder()
          .addNetworkInterceptor(new AllureOkHttp3()
              .setRequestTemplate("request.ftl")
              .setResponseTemplate("response.ftl")
          )
          .addNetworkInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
          .build())
      .build();
}
