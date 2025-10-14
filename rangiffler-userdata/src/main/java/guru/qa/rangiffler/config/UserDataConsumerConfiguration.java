package guru.qa.rangiffler.config;

import guru.qa.rangiffler.model.UserJson;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.ssl.SslBundles;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

@Configuration
public class UserDataConsumerConfiguration {
  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, UserJson> kafkaListenerContainerFactory(
      KafkaProperties kafkaProperties,
      SslBundles sslBundles
  ) {
    var deserializer = new JsonDeserializer<>(UserJson.class, false);
    var factory = new ConcurrentKafkaListenerContainerFactory<String, UserJson>();
    factory.setConsumerFactory(new DefaultKafkaConsumerFactory<>(
        kafkaProperties.buildConsumerProperties(sslBundles),
        new StringDeserializer(),
        deserializer
    ));
    return factory;
  }
}
