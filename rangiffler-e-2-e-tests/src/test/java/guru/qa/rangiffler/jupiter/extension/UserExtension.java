package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.db.UserCreationDbService;
import guru.qa.rangiffler.service.grpc.UserService;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

import static guru.qa.rangiffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.rangiffler.utils.RandomDataUtils.nonExistentUserName;

public class UserExtension implements
    ParameterResolver,
    BeforeEachCallback {

  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(UserExtension.class);

  private final UserCreationDbService userCreationService = new UserCreationDbService();
  private final UserService userService = new UserService();

  @Override
  public void beforeEach(ExtensionContext context) {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          final String username = !"".equals(user.username()) ? user.username() : nonExistentUserName();
          final String password = newValidPassword();
          final UserJson createdUser = userCreationService.createUser(username, password);
          List<UserJson> incomingRequests = Collections.emptyList();
          List<UserJson> outgoingRequests = Collections.emptyList();
          List<UserJson> friends = Collections.emptyList();

          if (user.incomingRequests() > 0) {
            incomingRequests = userService.createIncomingRequests(createdUser, user.incomingRequests());
          }
          if (user.outgoingRequests() > 0) {
            outgoingRequests = userService.createOutgoingRequests(createdUser, user.outgoingRequests());
          }
          if (user.friends() > 0) {
            friends = userService.createFriends(createdUser, user.friends());
          }

          final TestData testData = TestData.emptyTestData()
              .withPassword(password)
              .withIncomingRequests(incomingRequests)
              .withOutgoingRequests(outgoingRequests)
              .withFriends(friends);

          setContextUser(createdUser.withTestData(testData));
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return parameterContext.getParameter().getType().isAssignableFrom(UserJson.class);
  }

  @Override
  public UserJson resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return getContextUser();
  }

  public static @Nullable UserJson getContextUser() {
    final ExtensionContext context = TestMethodContextExtension.context();
    return context.getStore(NAMESPACE).get(context.getUniqueId(), UserJson.class);
  }

  static void setContextUser(UserJson user) {
    final ExtensionContext context = TestMethodContextExtension.context();
    context.getStore(NAMESPACE).put(context.getUniqueId(), user);
  }
}