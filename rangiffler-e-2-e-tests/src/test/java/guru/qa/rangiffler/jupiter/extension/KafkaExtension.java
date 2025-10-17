package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.service.kafka.KafkaConsumerService;
import org.junit.jupiter.api.extension.ExtensionContext;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class KafkaExtension implements SuiteExtension {
  private static final KafkaConsumerService KAFKA_CONSUMER_SERVICE = new KafkaConsumerService();
  private static final ExecutorService executor = Executors.newSingleThreadExecutor();

  @Override
  public void beforeSuite(ExtensionContext context) {
    executor.submit(KAFKA_CONSUMER_SERVICE);
    executor.shutdown();
  }

  @Override
  public void afterSuite() {
    KAFKA_CONSUMER_SERVICE.stop();
  }
}
