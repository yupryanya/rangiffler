package guru.qa.rangiffler.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.qa.rangiffler.data.LogEntry;
import guru.qa.rangiffler.repository.LogEntryRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Date;

@Slf4j
@Service
public class UserLogConsumer {

  private final LogEntryRepository repository;
  private final ObjectMapper objectMapper;

  public UserLogConsumer(LogEntryRepository repository, ObjectMapper objectMapper) {
    this.repository = repository;
    this.objectMapper = objectMapper;
  }

  @KafkaListener(topics = "user-logs", groupId = "logs-group")
  public void consume(String messageJson) {
    try {
      log.info("Starting processing message " + messageJson);
      LogEntry logEntry = objectMapper.readValue(messageJson, LogEntry.class);

      if (logEntry.getTimestamp() == null) {
        logEntry.setTimestamp(new Date());
      }

      repository.save(logEntry);

    } catch (Exception e) {
      log.info("Failed to consume log: " + e.getMessage());
      e.printStackTrace();
    }
  }
}