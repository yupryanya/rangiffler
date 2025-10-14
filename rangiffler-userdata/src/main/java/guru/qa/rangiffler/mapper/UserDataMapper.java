package guru.qa.rangiffler.mapper;

import guru.qa.rangiffler.FriendshipStatus;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

@Component
public class UserDataMapper {
  public UserResponse entityToGrpcResponse(UserEntity entity) {
    return UserResponse.newBuilder()
        .setId(entity.getId().toString())
        .setUsername(entity.getUsername())
        .setFirstname(Optional.ofNullable(entity.getFirstname()).orElse(""))
        .setSurname(Optional.ofNullable(entity.getSurname()).orElse(""))
        .setAvatar(bytesToString(entity.getAvatar()))
        .setCountryCode(entity.getCountryCode())
        .setStatus(FriendshipStatus.NONE)
        .build();
  }

  public UserResponse projectionToGrpcResponse(UserWithStatus projection) {
    return UserResponse.newBuilder()
        .setId(projection.getId().toString())
        .setUsername(projection.getUsername())
        .setFirstname(Optional.ofNullable(projection.getFirstname()).orElse(""))
        .setSurname(Optional.ofNullable(projection.getSurname()).orElse(""))
        .setAvatar(bytesToString(projection.getAvatar()))
        .setCountryCode(projection.getCountryCode())
        .setStatus(FriendshipStatus.valueOf(projection.getFriendshipState()))
        .build();
  }

  private String bytesToString(Object avatarField) {
    if (avatarField == null) return "";
    if (avatarField instanceof byte[] bytes) {
      return new String(bytes, StandardCharsets.UTF_8);
    }
    return avatarField.toString();
  }
}