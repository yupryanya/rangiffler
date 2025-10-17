package guru.qa.rangiffler.service.grpc;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.defs.FriendStatus;
import guru.qa.rangiffler.grpc.GrpcClients;
import guru.qa.rangiffler.service.grpc.mapper.UserMapper;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

class FriendshipGrpcClient {
  private final FriendshipServiceGrpc.FriendshipServiceBlockingStub stub = GrpcClients.friendshipService;
  private final UserMapper mapper = new UserMapper();

  UpdateFriendshipResponse updateFriendship(String requesterName, String addresseeName, FriendAction action) {
    try {
      UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
          .setRequesterName(requesterName)
          .setAddresseeName(addresseeName)
          .setAction(action)
          .build();
      return stub.updateFriendship(request);
    } catch (StatusRuntimeException e) {
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  List<UserResponse> getUsers(String requesterName, FriendStatus status) {
    List<UserResponse> allUsers = new ArrayList<>();
    int page = 0;
    int size = 100;
    ListUsersResponse response;

    do {
      ListUsersRequest request = ListUsersRequest.newBuilder()
          .setRequesterName(requesterName)
          .setStatus(mapper.friendStatusToGrpc(status))
          .setPage(page)
          .setSize(size)
          .build();

      try {
        response = stub.getUsers(request);
      } catch (StatusRuntimeException e) {
        throw new RuntimeException("The gRPC operation was cancelled", e);
      }

      allUsers.addAll(response.getUsersList());
      page++;
    } while (response.getUsersCount() == size);

    return allUsers;
  }
}