package guru.qa.rangiffler.service.grpc;

import guru.qa.rangiffler.UserDataServiceGrpc;
import guru.qa.rangiffler.UserResponse;
import guru.qa.rangiffler.UsernameRequest;
import guru.qa.rangiffler.grpc.GrpcClients;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class UserGrpcClient {
  private final UserDataServiceGrpc.UserDataServiceBlockingStub stub = GrpcClients.userdataService;

  UserResponse findUserByUsername(String username) {
    try {
      UsernameRequest request = UsernameRequest.newBuilder()
          .setUsername(username)
          .build();
      return stub.getUserByUsername(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}
