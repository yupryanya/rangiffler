package guru.qa.rangiffler.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.model.KafkaUser;
import guru.qa.rangiffler.utils.MapWithWait;
import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

public class KafkaConsumerService implements Runnable {
  private static final Config CFG = Config.getInstance();
  private static final AtomicBoolean isRunning = new AtomicBoolean(false);
  private static final Properties props = new Properties();
  private static final ObjectMapper om = new ObjectMapper();
  private static final MapWithWait<String, KafkaUser> storage = new MapWithWait<>();

  private final List<String> topics;
  private final Consumer<String, String> consumer;

  static {
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "e2e-tests-group");
    props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
  }

  public KafkaConsumerService() {
    this(CFG.kafkaTopics());
  }

  public KafkaConsumerService(List<String> topics) {
    this.topics = topics;
    this.consumer = new KafkaConsumer<>(props);
  }

  public static KafkaUser getUserByUsername(String username) throws InterruptedException {
    return storage.get(username, 7000L);
  }

  @Override
  public void run() {
    try {
      isRunning.set(true);
      consumer.subscribe(topics);
      while (isRunning.get()) {
        ConsumerRecords<String, String> records = consumer.poll(Duration.of(100, ChronoUnit.MILLIS));
        for (ConsumerRecord<String, String> record : records) {
          String userAsString = record.value();
          KafkaUser user = om.readValue(userAsString, KafkaUser.class);
          storage.put(user.username(), user);
        }
      }
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      consumer.close();
      Thread.currentThread().interrupt();
    }
  }

  public void stop() {
    isRunning.set(false);
  }
}