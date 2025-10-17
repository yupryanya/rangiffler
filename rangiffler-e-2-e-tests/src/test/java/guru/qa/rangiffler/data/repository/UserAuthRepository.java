package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.entity.UserAuthEntity;
import guru.qa.rangiffler.data.jpa.EntityManagers;
import io.qameta.allure.Step;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Optional;

@Slf4j
@ParametersAreNonnullByDefault
public class UserAuthRepository {
  private static final Config CFG = Config.getInstance();

  private final EntityManager entityManager = EntityManagers.em(CFG.authJdbcUrl());

  @Step("Create auth user")
  public @Nonnull UserAuthEntity create(UserAuthEntity authUser) {
    entityManager.joinTransaction();
    entityManager.persist(authUser);
    return authUser;
  }


  @Step("Find auth user by ID")
  public Optional<UserAuthEntity> findById(String id) {
    return Optional.ofNullable(
        entityManager.find(UserAuthEntity.class, id)
    );
  }

  @Step("Find auth user by username")
  public Optional<UserAuthEntity> findByUsername(String username) {
    try {
      return Optional.of(
          entityManager.createQuery("select u from AuthUserEntity u where u.username =: username", UserAuthEntity.class)
              .setParameter("username", username)
              .getSingleResult()
      );
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }
}