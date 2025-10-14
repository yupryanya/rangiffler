package guru.qa.rangiffler.data.projection;

import java.util.UUID;

public interface UserWithStatus {
  UUID getId();

  String getUsername();

  String getFirstname();

  String getSurname();

  byte[] getAvatar();

  String getCountryCode();

  String getFriendshipState();
}