package guru.qa.rangiffler.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.model.KafkaUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;

@Slf4j
public class KafkaProducerService {
  private static final Config CFG = Config.getInstance();
  private static final ObjectMapper om = new ObjectMapper();
  private static final Properties producerProperties = new Properties();

  private final Producer<String, String> producer;

  static {
    producerProperties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, CFG.kafkaAddress());
    producerProperties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    producerProperties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
  }

  public KafkaProducerService() {
    this.producer = new KafkaProducer<>(producerProperties);
  }

  public void sendMessage(String topic, KafkaUser userJson) {
    try {
      ProducerRecord<String, String> record = new ProducerRecord<>(topic, om.writeValueAsString(userJson));
      producer.send(record, (metadata, exception) -> {
        if (exception != null) {
          log.error("Failed to send message to Kafka topic '{}': {}",
              metadata.topic(), exception.getMessage(), exception);
        } else {
          log.info("Message sent to topic={}, partition={}, offset={}",
              metadata.topic(), metadata.partition(), metadata.offset());
        }
      });
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    } finally {
      producer.flush();
      producer.close();
    }
  }
}