package guru.qa.rangiffler.service.user;

import guru.qa.rangiffler.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
class UserDataGrpcClient {
  private final UserDataServiceGrpc.UserDataServiceBlockingStub stub;

  @Autowired
  public UserDataGrpcClient(UserDataServiceGrpc.UserDataServiceBlockingStub userdataServiceStub) {
    this.stub = userdataServiceStub;
  }

  public UserResponse getUserByUsername(String username) {
    try {
      return stub.getUserByUsername(
          UsernameRequest.newBuilder().setUsername(username).build()
      );
    } catch (StatusRuntimeException e) {
      log.error("### gRPC error in getUserByUsername", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public UserResponse getUserById(UUID userId) {
    try {
      return stub.getUserById(
          UserIdRequest.newBuilder().setUserId(userId.toString()).build()
      );
    } catch (StatusRuntimeException e) {
      log.error("### gRPC error in getUserById", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public UserResponse updateUser(UpdateUserRequest request) {
    try {
      return stub.updateUser(request);
    } catch (StatusRuntimeException e) {
      log.error("### gRPC error in updateUser", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}