package guru.qa.rangiffler.service.user;

import guru.qa.rangiffler.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class FriendshipGrpcClient {
  private final FriendshipServiceGrpc.FriendshipServiceBlockingStub stub;

  @Autowired
  public FriendshipGrpcClient(FriendshipServiceGrpc.FriendshipServiceBlockingStub stub) {
    this.stub = stub;
  }

  public ListUsersResponse getUsers(ListUsersRequest request) {
    try {
      return stub.getUsers(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public UpdateFriendshipResponse updateFriendship(UpdateFriendshipRequest request) {
    try {
      return stub.updateFriendship(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}
