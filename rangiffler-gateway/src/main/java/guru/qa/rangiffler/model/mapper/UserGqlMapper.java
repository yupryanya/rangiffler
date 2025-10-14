package guru.qa.rangiffler.model.mapper;

import guru.qa.rangiffler.FriendshipStatus;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.type.CountryGql;
import guru.qa.rangiffler.model.type.UserGql;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserGqlMapper {
  public UserGql userGqlFromGrpcMessage(UserResponse user, CountryGql location) {
    return new UserGql(
        UUID.fromString(user.getId()),
        user.getUsername(),
        user.getFirstname(),
        user.getSurname(),
        user.getAvatar(),
        friendStatusFromGrpcToGql(user.getStatus()),
        null,
        null,
        null,
        location
    );
  }

  public static FriendStatus friendStatusFromGrpcToGql(FriendshipStatus status) {
    return switch (status) {
      case FriendshipStatus.SENT_PENDING -> FriendStatus.INVITATION_SENT;
      case FriendshipStatus.RECEIVED_PENDING -> FriendStatus.INVITATION_RECEIVED;
      case FriendshipStatus.FRIEND -> FriendStatus.FRIEND;
      default -> null;
    };
  }

  public FriendshipStatus friendStatusFromGqlToGrpc(FriendStatus status) {
    return switch (status) {
      case NONE -> FriendshipStatus.NONE;
      case INVITATION_SENT -> FriendshipStatus.SENT_PENDING;
      case INVITATION_RECEIVED -> FriendshipStatus.RECEIVED_PENDING;
      case FRIEND -> FriendshipStatus.FRIEND;
    };
  }
}