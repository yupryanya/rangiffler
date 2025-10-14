package guru.qa.rangiffler.service;

import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.model.UserJson;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDataKafkaListener {
  private final UserDataRepository userDataRepository;

  @Autowired
  public UserDataKafkaListener(UserDataRepository userDataRepository) {
    this.userDataRepository = userDataRepository;
  }

  @Transactional
  @KafkaListener(topics = "users", groupId = "userdata")
  public void listener(@Payload UserJson user) {
    log.info("### Kafka consumer record received: {}", user);

    userDataRepository.findByUsername(user.username())
        .ifPresentOrElse(
            u -> log.info("### User '{}' already exists in DB, skipping", user.username()),
            () -> {
              UserEntity newUser = new UserEntity();
              newUser.setUsername(user.username());
              newUser.setCountryCode("ru");
              UserEntity savedUser = userDataRepository.save(newUser);

              log.info("### User '{}' successfully saved with id: {}",
                  savedUser.getUsername(), savedUser.getId());
            }
        );
  }
}
