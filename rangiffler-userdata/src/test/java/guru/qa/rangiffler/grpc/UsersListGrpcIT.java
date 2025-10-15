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

import static org.assertj.core.api.Assertions.assertThat;

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

    assertThat(response.getUsersList()).hasSize(1);
    assertThat(response.getUsersList().getFirst().getUsername()).isEqualTo("anna");
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

    assertThat(response.getUsersList()).hasSize(1);
    assertThat(response.getUsersList().getFirst().getUsername()).isEqualTo("john");
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

    assertThat(response.getUsersList()).hasSize(1);
    assertThat(response.getUsersList().getFirst().getUsername()).isEqualTo("bob");
  }
}
