package guru.qa.rangiffler.grpc.service;

import guru.qa.rangiffler.UpdateUserRequest;
import guru.qa.rangiffler.UserIdRequest;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.UsernameRequest;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.mapper.UserDataMapper;
import guru.qa.rangiffler.service.UserDataGrpcService;
import io.grpc.stub.StreamObserver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDataGrpcServiceTest {

  @Mock
  private UserDataRepository userDataRepository;

  @Mock
  private UserDataMapper mapper;

  @Mock
  private StreamObserver<UserResponse> userResponseObserver;

  private UserDataGrpcService service;

  private UserEntity testUser;
  private UserResponse testUserResponse;

  @BeforeEach
  void setUp() {
    service = new UserDataGrpcService(userDataRepository, mapper);

    testUser = new UserEntity();
    testUser.setId(UUID.randomUUID());
    testUser.setUsername("testuser");
    testUser.setFirstname("Test");
    testUser.setSurname("User");
    testUser.setAvatar("avatar_data".getBytes());
    testUser.setCountryCode("by");

    testUserResponse = UserResponse.newBuilder()
        .setId(testUser.getId().toString())
        .setUsername(testUser.getUsername())
        .setFirstname(testUser.getFirstname())
        .setSurname(testUser.getSurname())
        .setAvatar(new String(testUser.getAvatar()))
        .setCountryCode(testUser.getCountryCode())
        .build();
  }

  @Test
  void shouldReturnUserWhenUsernameExists() {
    UsernameRequest request = UsernameRequest.newBuilder().setUsername("testuser").build();

    when(userDataRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
    when(mapper.entityToGrpcResponse(testUser)).thenReturn(testUserResponse);

    assertThatCode(() -> service.getUserByUsername(request, userResponseObserver))
        .doesNotThrowAnyException();

    verify(userDataRepository).findByUsername("testuser");
    verify(mapper).entityToGrpcResponse(testUser);
    verify(userResponseObserver).onNext(testUserResponse);
    verify(userResponseObserver).onCompleted();
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenUsernameDoesNotExist() {
    UsernameRequest request = UsernameRequest.newBuilder().setUsername("nonexistent").build();
    when(userDataRepository.findByUsername("nonexistent")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getUserByUsername(request, userResponseObserver))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found: nonexistent");

    verify(userDataRepository).findByUsername("nonexistent");
    verifyNoInteractions(mapper, userResponseObserver);
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenUsernameIsEmpty() {
    UsernameRequest request = UsernameRequest.newBuilder().setUsername("").build();
    when(userDataRepository.findByUsername("")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getUserByUsername(request, userResponseObserver))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found: ");
  }

  @Test
  void shouldReturnUserWhenIdExists() {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(testUser.getId().toString()).build();

    when(userDataRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
    when(mapper.entityToGrpcResponse(testUser)).thenReturn(testUserResponse);

    assertThatCode(() -> service.getUserById(request, userResponseObserver))
        .doesNotThrowAnyException();

    verify(userDataRepository).findById(testUser.getId());
    verify(mapper).entityToGrpcResponse(testUser);
    verify(userResponseObserver).onNext(testUserResponse);
    verify(userResponseObserver).onCompleted();
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenIdDoesNotExist() {
    UUID nonExistentId = UUID.randomUUID();
    UserIdRequest request = UserIdRequest.newBuilder().setUserId(nonExistentId.toString()).build();
    when(userDataRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.getUserById(request, userResponseObserver))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found: " + nonExistentId);

    verify(userDataRepository).findById(nonExistentId);
    verifyNoInteractions(mapper, userResponseObserver);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenUserIdFormatIsInvalid() {
    UserIdRequest request = UserIdRequest.newBuilder().setUserId("invalid-uuid").build();

    assertThatThrownBy(() -> service.getUserById(request, userResponseObserver))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(userDataRepository, mapper, userResponseObserver);
  }

  @Test
  void shouldUpdateAndReturnUserWhenUserExists() {
    String userId = testUser.getId().toString();
    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setId(userId)
        .setFirstname("Updated")
        .setSurname("Name")
        .setAvatar("new_avatar_data")
        .setCountryCode("US")
        .build();

    UserEntity updatedUser = new UserEntity();
    updatedUser.setId(testUser.getId());
    updatedUser.setUsername(testUser.getUsername());
    updatedUser.setFirstname("Updated");
    updatedUser.setSurname("Name");
    updatedUser.setAvatar("new_avatar_data".getBytes());
    updatedUser.setCountryCode("US");

    UserResponse updatedResponse = UserResponse.newBuilder()
        .setId(userId)
        .setUsername(testUser.getUsername())
        .setFirstname("Updated")
        .setSurname("Name")
        .setAvatar("new_avatar_data")
        .setCountryCode("US")
        .build();

    when(userDataRepository.findById(testUser.getId())).thenReturn(Optional.of(testUser));
    when(userDataRepository.save(any(UserEntity.class))).thenReturn(updatedUser);
    when(mapper.entityToGrpcResponse(updatedUser)).thenReturn(updatedResponse);

    assertThatCode(() -> service.updateUser(request, userResponseObserver))
        .doesNotThrowAnyException();

    verify(userDataRepository).findById(testUser.getId());
    verify(userDataRepository).save(argThat(user ->
        user.getFirstname().equals("Updated") &&
        user.getSurname().equals("Name") &&
        new String(user.getAvatar()).equals("new_avatar_data") &&
        user.getCountryCode().equals("US")
    ));
    verify(mapper).entityToGrpcResponse(updatedUser);
    verify(userResponseObserver).onNext(updatedResponse);
    verify(userResponseObserver).onCompleted();
  }

  @Test
  void shouldThrowUserNotFoundExceptionWhenUpdatingNonExistentUser() {
    UUID nonExistentId = UUID.randomUUID();
    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setId(nonExistentId.toString())
        .setFirstname("Updated")
        .setSurname("Name")
        .setAvatar("avatar")
        .setCountryCode("US")
        .build();

    when(userDataRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.updateUser(request, userResponseObserver))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessage("User not found: " + nonExistentId);

    verify(userDataRepository).findById(nonExistentId);
    verify(userDataRepository, never()).save(any());
    verifyNoInteractions(mapper, userResponseObserver);
  }

  @Test
  void shouldThrowIllegalArgumentExceptionWhenUpdatingUserWithInvalidId() {
    UpdateUserRequest request = UpdateUserRequest.newBuilder()
        .setId("invalid-uuid")
        .setFirstname("Updated")
        .setSurname("Name")
        .setAvatar("avatar")
        .setCountryCode("US")
        .build();

    assertThatThrownBy(() -> service.updateUser(request, userResponseObserver))
        .isInstanceOf(IllegalArgumentException.class);

    verifyNoInteractions(userDataRepository, mapper, userResponseObserver);
  }
}