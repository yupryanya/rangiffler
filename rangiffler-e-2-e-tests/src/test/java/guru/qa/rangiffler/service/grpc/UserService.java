package guru.qa.rangiffler.service.grpc;

import guru.qa.rangiffler.FriendAction;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.defs.FriendStatus;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.db.UserCreationDbService;
import guru.qa.rangiffler.service.grpc.mapper.UserMapper;
import io.qameta.allure.Step;
import lombok.extern.slf4j.Slf4j;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.codeborne.selenide.Selenide.sleep;
import static guru.qa.rangiffler.utils.RandomDataUtils.newValidPassword;
import static guru.qa.rangiffler.utils.RandomDataUtils.nonExistentUserName;

@Slf4j
public class UserService {
  private final UserCreationDbService userCreationService = new UserCreationDbService();
  private final FriendshipGrpcClient friendshipClient = new FriendshipGrpcClient();
  private final UserGrpcClient userClient = new UserGrpcClient();

  private final UserMapper mapper = new UserMapper();

  @Step("Create friends with GRPC")
  public @Nonnull List<UserJson> createFriends(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedFriends = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserJson randomUser = userCreationService.createUser(nonExistentUserName(), newValidPassword());
      friendshipClient.updateFriendship(user.username(), randomUser.username(), FriendAction.ADD);
      sleep(100);
      friendshipClient.updateFriendship(randomUser.username(), user.username(), FriendAction.ACCEPT);
      addedFriends.add(randomUser);
    }
    user.testData().friends().addAll(addedFriends);
    return addedFriends;
  }

  @Step("Create {count} outgoing requests with GRPC")
  public @Nonnull List<UserJson> createOutgoingRequests(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedRequests = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserJson randomUser = userCreationService.createUser(nonExistentUserName(), newValidPassword());
      friendshipClient.updateFriendship(user.username(), randomUser.username(), FriendAction.ADD);
      addedRequests.add(randomUser);
    }
    user.testData().outgoingRequests().addAll(addedRequests);
    return addedRequests;
  }

  @Step("Create {count} incoming requests with GRPC")
  public @Nonnull List<UserJson> createIncomingRequests(UserJson user, int count) {
    if (count < 1) {
      throw new IllegalArgumentException("Count must be greater than 0");
    }
    List<UserJson> addedRequests = new ArrayList<>();
    for (int i = 0; i < count; i++) {
      UserJson randomUser = userCreationService.createUser(nonExistentUserName(), newValidPassword());
      friendshipClient.updateFriendship(randomUser.username(), user.username(), FriendAction.ADD);
      addedRequests.add(randomUser);
    }
    user.testData().incomingRequests().addAll(addedRequests);
    return addedRequests;
  }

  @Step("Find user by username with GRPC")
  public Optional<UserJson> findUserByUsername(String username) {
    if (username == null || username.isBlank()) {
      return Optional.empty();
    }
    UserResponse response = userClient.findUserByUsername(username);
    return Optional.ofNullable(mapper.toUserJson(response));
  }

  @Step("Get related users with GRPC")
  public List<UserJson> getRelated(String username, FriendStatus status) {
    List<UserResponse> response = friendshipClient.getUsers(username, status);
    return response.stream()
        .map(mapper::toUserJson)
        .toList();
  }
}