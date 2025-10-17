package guru.qa.rangiffler.service.db;

import com.atomikos.icatch.jta.UserTransactionImp;
import guru.qa.rangiffler.data.entity.Authority;
import guru.qa.rangiffler.data.entity.AuthorityEntity;
import guru.qa.rangiffler.data.entity.UserAuthEntity;
import guru.qa.rangiffler.data.entity.UserDataEntity;
import guru.qa.rangiffler.data.repository.UserAuthRepository;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.model.UserJson;
import io.qameta.allure.Step;
import jakarta.transaction.UserTransaction;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.Arrays;

import static guru.qa.rangiffler.data.entity.UserDataEntity.fromEntity;
import static guru.qa.rangiffler.model.UserJson.generateUserJson;

@ParametersAreNonnullByDefault
public class UserCreationDbService {
  private final UserAuthRepository userAuth = new UserAuthRepository();
  private final UserDataRepository userData = new UserDataRepository();

  @Step("Create user in UserAuth an UserData databases")
  public UserJson createUser(String username, String password) {
    UserJson user = generateUserJson(username, password);
    UserTransaction tx = new UserTransactionImp();
    try {
      tx.begin();
      UserAuthEntity authUserEntity = UserAuthEntity.fromJson(user);
      authUserEntity.setAuthorities(Arrays.stream(Authority.values())
          .map(authority -> {
            AuthorityEntity authAuthority = new AuthorityEntity();
            authAuthority.setUser(authUserEntity);
            authAuthority.setAuthority(authority);
            return authAuthority;
          })
          .toList());
      userAuth.create(authUserEntity);
      UserDataEntity userEntity = userData.create(UserDataEntity.fromJson(user));

      tx.commit();

      UserJson userJson = fromEntity(userEntity);
      return userJson.withEmptyTestData().withPassword(password);
    } catch (Exception e) {
      try {
        tx.rollback();
      } catch (Exception rollbackEx) {
        rollbackEx.addSuppressed(e);
        throw new RuntimeException("Rollback failed after error: " + e.getMessage(), rollbackEx);
      }
      throw new RuntimeException("Error creating user in both DBs", e);
    }
  }

  @Step("Wait for user '{username}' to appear in UserData database")
  public UserDataEntity waitForUser(String username) throws InterruptedException {
    long maxWaitMs = 5000L;
    long pollMs = 200L;
    long startTime = System.currentTimeMillis();

    while (true) {
      if (!(System.currentTimeMillis() - startTime < maxWaitMs)) break;
      UserDataEntity user = userData.findByUsername(username);
      if (user != null) {
        return user;
      }
      Thread.sleep(pollMs);
    }
    return null;
  }
}