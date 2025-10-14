package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.FriendshipServiceGrpc;
import guru.qa.rangiffler.FriendshipStatus;
import guru.qa.rangiffler.ListUsersRequest;
import guru.qa.rangiffler.ListUsersResponse;
import guru.qa.rangiffler.grpc.config.GrpcTestStubsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@Import(GrpcTestStubsConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UsersListGrpcIT {
  @Autowired
  private FriendshipServiceGrpc.FriendshipServiceBlockingStub friendshipStub;

  @Sql(scripts = "/userFriendsShouldBeReturned.sql")
  @Test
  void userFriendsShouldBeReturned() {
    ListUsersRequest request = ListUsersRequest.newBuilder()
        .setRequesterName("alice")
        .setStatus(FriendshipStatus.FRIEND)
        .setPage(0)
        .setSize(10)
        .build();

    ListUsersResponse response = friendshipStub.getUsers(request);

    assertEquals(1, response.getUsersList().size());
    assertEquals("anna", response.getUsersList().getFirst().getUsername());
  }

  @Sql(scripts = "/userFriendsShouldBeReturned.sql")
  @Test
  void userOutgoingRequestsShouldBeReturned() {
    ListUsersRequest request = ListUsersRequest.newBuilder()
        .setRequesterName("alice")
        .setStatus(FriendshipStatus.SENT_PENDING)
        .setPage(0)
        .setSize(10)
        .build();

    ListUsersResponse response = friendshipStub.getUsers(request);

    assertEquals(1, response.getUsersList().size());
    assertEquals("john", response.getUsersList().getFirst().getUsername());
  }

  @Sql(scripts = "/userFriendsShouldBeReturned.sql")
  @Test
  void userIncomingRequestsShouldBeReturned() {
    ListUsersRequest request = ListUsersRequest.newBuilder()
        .setRequesterName("alice")
        .setStatus(FriendshipStatus.RECEIVED_PENDING)
        .setPage(0)
        .setSize(10)
        .build();

    ListUsersResponse response = friendshipStub.getUsers(request);

    assertEquals(1, response.getUsersList().size());
    assertEquals("bob", response.getUsersList().getFirst().getUsername());
  }
}
