package guru.qa.rangiffler.service;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.data.FriendshipEntityStatus;
import guru.qa.rangiffler.data.entity.FriendshipEntity;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import guru.qa.rangiffler.data.repository.FriendshipRepository;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.ex.SelfFriendshipException;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.mapper.UserDataMapper;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
public class FriendshipGrpcService extends FriendshipServiceGrpc.FriendshipServiceImplBase {
  private final FriendshipRepository friendshipRepository;
  private final UserDataRepository userDataRepository;
  private final UserDataMapper mapper;

  @Autowired
  public FriendshipGrpcService(FriendshipRepository friendshipRepository,
                               UserDataRepository userDataRepository,
                               UserDataMapper mapper) {
    this.friendshipRepository = friendshipRepository;
    this.userDataRepository = userDataRepository;
    this.mapper = mapper;
  }

  @Override
  @Transactional(readOnly = true)
  public void getUsers(ListUsersRequest request,
                       StreamObserver<ListUsersResponse> responseObserver) {
    String userName = request.getRequesterName();
    Page<UserWithStatus> pageResult = friendshipRepository.findAllWithFriendshipState(
        userName,
        request.getStatus() == FriendshipStatus.NONE ? null
            : FriendshipEntityStatus.valueOf(request.getStatus().name()),
        request.getSearchQuery().isBlank() ? null : request.getSearchQuery(),
        PageRequest.of(request.getPage(), request.getSize())
    );

    ListUsersResponse response = ListUsersResponse.newBuilder()
        .addAllUsers(pageResult.getContent().stream()
            .map(mapper::projectionToGrpcResponse)
            .toList())
        .setPage(request.getPage())
        .setSize(request.getSize())
        .setTotal((int) pageResult.getTotalElements())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  @Transactional
  public void updateFriendship(UpdateFriendshipRequest request,
                               StreamObserver<UpdateFriendshipResponse> responseObserver) {
    UserEntity requester = findUserOrThrow(request.getRequesterName());
    UserEntity addressee = findUserOrThrow(request.getAddresseeName());

    if (requester.getId().equals(addressee.getId())) {
      throw new SelfFriendshipException("Users cannot send friend requests to themselves");
    }

    FriendAction action = request.getAction();

    FriendshipEntity sent = friendshipRepository.findByUserAndFriend(requester, addressee).orElse(null);
    FriendshipEntity received = friendshipRepository.findByUserAndFriend(addressee, requester).orElse(null);

    switch (action) {
      case ADD -> {
        if (sent != null || received != null) {
          throw new IllegalStateException("Friend request already exists between users");
        }
        sent = FriendshipEntity.requester(requester, addressee);
        received = FriendshipEntity.addressee(addressee, requester);
        friendshipRepository.save(sent);
        friendshipRepository.save(received);
      }
      case ACCEPT -> {
        requireExisting(sent, requester, addressee);
        requireExisting(received, addressee, requester);
        sent.acceptRequest();
        received.acceptRequest();
      }
      case REJECT -> {
        requireExisting(sent, requester, addressee);
        requireExisting(received, addressee, requester);
        friendshipRepository.delete(sent);
        friendshipRepository.delete(received);
        sent = null;
      }
      case DELETE -> {
        requireExisting(sent, requester, addressee);
        requireExisting(received, addressee, requester);
        friendshipRepository.delete(sent);
        friendshipRepository.delete(received);
        sent = null;
      }
      default -> throw new UnsupportedOperationException("Unknown action: " + action);
    }

    UpdateFriendshipResponse response = UpdateFriendshipResponse.newBuilder()
        .setAddresseeName(addressee.getUsername())
        .setStatus(resolveResponseStatus(sent))
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  private UserEntity findUserOrThrow(String username) {
    return userDataRepository.findByUsername(username)
        .orElseThrow(() -> new UserNotFoundException("User not found: " + username));
  }

  private void requireExisting(FriendshipEntity entity, UserEntity user, UserEntity friend) {
    if (entity == null) {
      throw new IllegalStateException("No friendship exists between %s and %s"
          .formatted(user.getUsername(), friend.getUsername()));
    }
  }

  private FriendshipStatus resolveResponseStatus(FriendshipEntity sent) {
    return (sent == null) ? FriendshipStatus.NONE
        : FriendshipStatus.valueOf(sent.getState().name());
  }
}