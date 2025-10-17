package guru.qa.rangiffler.service.grpc.mapper;

import guru.qa.rangiffler.FriendshipStatus;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.defs.FriendStatus;
import guru.qa.rangiffler.model.TestData;
import guru.qa.rangiffler.model.UserJson;

import java.util.UUID;

public class UserMapper {

  public static FriendStatus friendStatusFromGrpc(FriendshipStatus status) {
    return switch (status) {
      case FriendshipStatus.SENT_PENDING -> FriendStatus.INVITATION_SENT;
      case FriendshipStatus.RECEIVED_PENDING -> FriendStatus.INVITATION_RECEIVED;
      case FriendshipStatus.FRIEND -> FriendStatus.FRIEND;
      default -> null;
    };
  }

  public FriendshipStatus friendStatusToGrpc(FriendStatus status) {
    return switch (status) {
      case NONE -> FriendshipStatus.NONE;
      case INVITATION_SENT -> FriendshipStatus.SENT_PENDING;
      case INVITATION_RECEIVED -> FriendshipStatus.RECEIVED_PENDING;
      case FRIEND -> FriendshipStatus.FRIEND;
    };
  }

  public UserJson toUserJson(UserResponse user) {
    return new UserJson(
        UUID.fromString(user.getId()),
        user.getUsername(),
        user.getFirstname(),
        user.getSurname(),
        user.getAvatar(),
        user.getCountryCode(),
        TestData.emptyTestData()
    );
  }
}