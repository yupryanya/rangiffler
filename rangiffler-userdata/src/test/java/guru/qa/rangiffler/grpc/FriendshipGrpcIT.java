package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.*;
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
class FriendshipGrpcIT {
  @Autowired
  private FriendshipServiceGrpc.FriendshipServiceBlockingStub friendshipStub;

  @Sql(scripts = "/sendInvitationShouldWork.sql")
  @Test
  void sendInvitationShouldUpdateStatus() {
    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ADD)
        .build();

    UpdateFriendshipResponse response = friendshipStub.updateFriendship(request);

    assertThat(response.getAddresseeName()).isEqualTo("bob");
    assertThat(response.getStatus()).isEqualTo(FriendshipStatus.SENT_PENDING);
  }

  @Sql(scripts = "/sendInvitationShouldWork.sql")
  @Test
  void sendInvitationWithInexistentUserShouldFail() {
    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("anna")
        .setAddresseeName("bob")
        .setAction(FriendAction.ADD)
        .build();

    UpdateFriendshipResponse response = friendshipStub.updateFriendship(request);

    assertThat(response.getAddresseeName()).isEqualTo("bob");
    assertThat(response.getStatus()).isEqualTo(FriendshipStatus.SENT_PENDING);
  }

  @Sql(scripts = "/acceptInvitationShouldWork.sql")
  @Test
  void acceptInvitationShouldUpdateStatus() {
    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("bob")
        .setAddresseeName("alice")
        .setAction(FriendAction.ACCEPT)
        .build();

    UpdateFriendshipResponse response = friendshipStub.updateFriendship(request);

    assertThat(response.getAddresseeName()).isEqualTo("alice");
    assertThat(response.getStatus()).isEqualTo(FriendshipStatus.FRIEND);
  }

  @Sql(scripts = "/declineInvitationShouldWork.sql")
  @Test
  void declineInvitationShouldUpdateStatus() {
    UpdateFriendshipRequest request = UpdateFriendshipRequest.newBuilder()
        .setRequesterName("bob")
        .setAddresseeName("alice")
        .setAction(FriendAction.REJECT)
        .build();

    UpdateFriendshipResponse response = friendshipStub.updateFriendship(request);

    assertThat(response.getAddresseeName()).isEqualTo("alice");
    assertThat(response.getStatus()).isEqualTo(FriendshipStatus.NONE);
  }
}