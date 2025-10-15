package guru.qa.rangiffler.service.user;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.input.FriendshipInput;
import guru.qa.rangiffler.model.input.UserInput;
import guru.qa.rangiffler.model.mapper.UserGqlMapper;
import guru.qa.rangiffler.model.type.CountryGql;
import guru.qa.rangiffler.model.type.UserGql;
import guru.qa.rangiffler.service.country.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {
  private final UserDataGrpcClient userDataGrpcClient;
  private final FriendshipGrpcClient friendshipGrpcClient;
  private final CountryService countryService;
  private final UserGqlMapper mapper;

  @Autowired
  public UserService(UserDataGrpcClient userDataGrpcClient,
                     FriendshipGrpcClient friendshipGrpcClient,
                     CountryService countryService,
                     UserGqlMapper mapper) {
    this.userDataGrpcClient = userDataGrpcClient;
    this.friendshipGrpcClient = friendshipGrpcClient;
    this.countryService = countryService;
    this.mapper = mapper;
  }

  public @Nonnull UUID getUserId(String username) {
    if (username == null) {
      throw new IllegalArgumentException("Username must not be null");
    }
    UUID userId = getUserByUsername(username).id();
    log.debug("Resolved userId {} for username '{}'", userId, username);
    return userId;
  }

  public @Nonnull UserGql getUserByUsername(String username) {
    UserResponse grpcUser = userDataGrpcClient.getUserByUsername(username);
    CountryGql country = countryService.getCountryByCode(grpcUser.getCountryCode());
    UserGql userGql = mapper.userGqlFromGrpcMessage(grpcUser, country);
    log.debug("Fetched user by username '{}': {}", username, userGql);
    return userGql;
  }

  @Nonnull
  public UserGql getUserById(UUID userId) {
    UserResponse grpcUser = userDataGrpcClient.getUserById(userId);
    CountryGql country = countryService.getCountryByCode(grpcUser.getCountryCode());
    UserGql userGql = mapper.userGqlFromGrpcMessage(grpcUser, country);
    log.debug("Fetched user by id {}: {}", userId, userGql);
    return userGql;
  }

  public Page<UserGql> getAllUsers(String username, FriendStatus status, Pageable pageable, String searchQuery) {
    ListUsersRequest request = ListUsersRequest.newBuilder()
        .setRequesterName(username)
        .setStatus(mapper.friendStatusFromGqlToGrpc(status))
        .setPage(pageable.getPageNumber())
        .setSize(pageable.getPageSize())
        .setSearchQuery(searchQuery)
        .build();
    ListUsersResponse response = friendshipGrpcClient.getUsers(request);
    List<UserGql> users = response.getUsersList().stream()
        .map(user -> mapper.userGqlFromGrpcMessage(user, countryService.getCountryByCode(user.getCountryCode())))
        .toList();
    log.debug("Fetched {} users for requester '{}', status={}, searchQuery='{}'", users.size(), username, status, searchQuery);
    return new PageImpl<>(users, pageable, response.getTotal());
  }

  public UserGql updateFriendship(String requesterName, FriendshipInput input) {
    String addresseeName = getUserById(input.user()).username();
    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName(requesterName)
        .setAddresseeName(addresseeName)
        .setAction(FriendAction.valueOf(input.action().name()))
        .build();
    UpdateFriendshipResponse response = friendshipGrpcClient.updateFriendship(request);
    UserGql updatedUser = getUserByUsername(response.getAddresseeName());
    log.info("User '{}' updated friendship with '{}', action={}", requesterName, addresseeName, input.action());
    return updatedUser;
  }

  public UserGql updateUserData(String username, UserInput input) {
    UUID userId = getUserId(username);
    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setId(userId.toString())
        .setFirstname(input.firstname())
        .setSurname(input.surname())
        .setAvatar(input.avatar())
        .setCountryCode(input.location().code())
        .build();
    UserResponse grpcUser = userDataGrpcClient.updateUser(request);
    CountryGql country = countryService.getCountryByCode(grpcUser.getCountryCode());
    UserGql updatedUser = mapper.userGqlFromGrpcMessage(grpcUser, country);
    log.info("Updated user data for '{}': {}", username, updatedUser);
    return updatedUser;
  }
}