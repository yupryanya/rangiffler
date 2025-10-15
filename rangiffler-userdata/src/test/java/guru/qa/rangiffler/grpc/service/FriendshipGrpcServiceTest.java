package guru.qa.rangiffler.grpc.service;

import guru.qa.rangiffler.FriendAction;
import guru.qa.rangiffler.UpdateFriendshipRequest;
import guru.qa.rangiffler.UpdateFriendshipResponse;
import guru.qa.rangiffler.data.entity.FriendshipEntity;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.repository.FriendshipRepository;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.ex.SelfFriendshipException;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.mapper.UserDataMapper;
import guru.qa.rangiffler.service.FriendshipGrpcService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class FriendshipGrpcServiceTest {

  @Mock
  private FriendshipRepository friendshipRepository;

  @Mock
  private UserDataRepository userDataRepository;

  @Mock
  private UserDataMapper mapper;

  @Mock
  private StreamObserver<UpdateFriendshipResponse> mockObserver;

  private FriendshipGrpcService service;

  private UserEntity anna;
  private UserEntity bob;

  @BeforeEach
  void setUp() {
    service = new FriendshipGrpcService(friendshipRepository, userDataRepository, mapper);

    anna = new UserEntity();
    anna.setId(UUID.randomUUID());
    anna.setUsername("anna");

    bob = new UserEntity();
    bob.setId(UUID.randomUUID());
    bob.setUsername("bob");
  }

  @Test
  void shouldThrowSelfFriendshipException() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("anna")
        .setAction(FriendAction.ADD)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(SelfFriendshipException.class);
  }

  @Test
  void shouldThrowUserNotFoundExceptionForRequester() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.empty());

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ADD)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void shouldThrowUserNotFoundExceptionForAddressee() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.empty());

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ADD)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(UserNotFoundException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenAddingExistingFriendship() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    when(friendshipRepository.findByUserAndFriend(anna, bob)).thenReturn(Optional.of(new FriendshipEntity()));

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ADD)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenAcceptingNonExistingFriendship() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    when(friendshipRepository.findByUserAndFriend(anna, bob)).thenReturn(Optional.empty());

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ACCEPT)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenDeletingNonExistingFriendship() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    when(friendshipRepository.findByUserAndFriend(anna, bob)).thenReturn(Optional.empty());

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.DELETE)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(IllegalStateException.class);
  }

  @Test
  void shouldThrowUnsupportedOperationExceptionForUnknownAction() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.of(bob));

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setActionValue(999)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(UnsupportedOperationException.class);
  }

  @Test
  void shouldThrowIllegalStateExceptionWhenPartialFriendshipExists() {
    when(userDataRepository.findByUsername("anna")).thenReturn(Optional.of(anna));
    when(userDataRepository.findByUsername("bob")).thenReturn(Optional.of(bob));
    when(friendshipRepository.findByUserAndFriend(anna, bob)).thenReturn(Optional.of(new FriendshipEntity()));
    when(friendshipRepository.findByUserAndFriend(bob, anna)).thenReturn(Optional.empty());

    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ACCEPT)
        .build();

    assertThatThrownBy(() -> service.updateFriendship(request, mockObserver))
        .isInstanceOf(IllegalStateException.class);
  }
}
