package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.UserDataEntity;
import guru.qa.rangiffler.data.jpa.EntityManagers;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

@Slf4j
@ParametersAreNonnullByDefault
public class UserDataRepository {
  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.userdataJdbcUrl());

  public @Nonnull UserDataEntity create(UserDataEntity user) {
    entityManager.joinTransaction();
    entityManager.persist(user);
    return user;
  }

  public UserDataEntity findByUsername(String username) {
    return entityManager.createQuery(
            "SELECT u FROM UserDataEntity u WHERE u.username = :username", UserDataEntity.class)
        .setParameter("username", username)
        .getResultStream()
        .findFirst()
        .orElse(null);
  }
}
