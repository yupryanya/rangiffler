package guru.qa.rangiffler.service;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.repository.UserDataRepository;
import guru.qa.rangiffler.ex.UserNotFoundException;
import guru.qa.rangiffler.mapper.UserDataMapper;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class UserDataGrpcService extends UserDataServiceGrpc.UserDataServiceImplBase {
  private final UserDataRepository userDataRepository;
  private final UserDataMapper mapper;

  @Autowired
  public UserDataGrpcService(UserDataRepository userDataRepository,
                             UserDataMapper mapper) {
    this.userDataRepository = userDataRepository;
    this.mapper = mapper;
  }

  @Override
  public void getUserByUsername(UsernameRequest request, StreamObserver<UserResponse> responseObserver) {
    UserEntity user = userDataRepository.findByUsername(request.getUsername())
        .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUsername()));

    UserResponse response = mapper.entityToGrpcResponse(user);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void getUserById(UserIdRequest request, StreamObserver<UserResponse> responseObserver) {
    UserEntity user = userDataRepository.findById(UUID.fromString(request.getUserId()))
        .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getUserId()));

    UserResponse response = mapper.entityToGrpcResponse(user);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  public void updateUser(UpdateUserRequest request, StreamObserver<UserResponse> responseObserver) {
    UserEntity user = userDataRepository.findById(UUID.fromString(request.getId()))
        .orElseThrow(() -> new UserNotFoundException("User not found: " + request.getId()));

    if (request.hasFirstname()) {
      user.setFirstname(request.getFirstname());
    }

    if (request.hasSurname()) {
      user.setSurname(request.getSurname());
    }

    if (!request.getAvatar().isEmpty()) {
      user.setAvatar(request.getAvatar().getBytes());
    }

    if (!request.getCountryCode().isEmpty()) {
      user.setCountryCode(request.getCountryCode());
    }

    UserEntity saved = userDataRepository.save(user);

    UserResponse response = mapper.entityToGrpcResponse(saved);
    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }
}