package guru.qa.rangiffler.test.kafka;

import guru.qa.rangiffler.api.AuthApi;
import guru.qa.rangiffler.api.core.RestClient;
import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.UserDataEntity;
import guru.qa.rangiffler.jupiter.annotation.meta.KafkaTest;
import guru.qa.rangiffler.model.KafkaUser;
import guru.qa.rangiffler.service.db.UserCreationDbService;
import guru.qa.rangiffler.service.kafka.KafkaConsumerService;
import guru.qa.rangiffler.service.kafka.KafkaProducerService;
import guru.qa.rangiffler.utils.RandomDataUtils;
import okhttp3.logging.HttpLoggingInterceptor;
import org.junit.jupiter.api.Test;
import retrofit2.converter.jackson.JacksonConverterFactory;

import static org.assertj.core.api.Assertions.assertThat;

@KafkaTest
public class AuthKafkaTest {
  private static final Config CFG = Config.getInstance();

  private final AuthApi authApi = new RestClient(
      CFG.authUrl(),
      true,
      JacksonConverterFactory.create(),
      HttpLoggingInterceptor.Level.BODY
  ).getRetrofit().create(AuthApi.class);

  private final UserCreationDbService userCreationService = new UserCreationDbService();
  private final KafkaProducerService kafkaProducerService = new KafkaProducerService();

  @Test
  void shouldProduceUserToKafka() throws Exception {
    final String username = RandomDataUtils.nonExistentUserName();
    final String password = RandomDataUtils.newValidPassword();

    authApi.requestRegisterForm().execute();
    authApi.register(
        username,
        password,
        password,
        ThreadSafeCookieStore.INSTANCE.getCookieValue("XSRF-TOKEN")
    ).execute();

    KafkaUser userFromKafka = KafkaConsumerService.getUserByUsername(username);
    assertThat(userFromKafka.username()).isEqualTo(username);
  }

  @Test
  void shouldWriteUserToDbFromKafka() throws Exception {
    final String username = RandomDataUtils.nonExistentUserName();

    kafkaProducerService.sendMessage("users", new KafkaUser(username));

    UserDataEntity userFromDb = userCreationService.waitForUser(username);
    assertThat(userFromDb.getUsername()).isEqualTo(username);
  }
}