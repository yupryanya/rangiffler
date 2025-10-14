package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.PhotoServiceGrpc;
import guru.qa.rangiffler.StatsResponse;
import guru.qa.rangiffler.UsersStatRequest;
import guru.qa.rangiffler.grpc.config.GrpcTestStubsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(GrpcTestStubsConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class StatsGrpcIT {

  @Autowired
  private PhotoServiceGrpc.PhotoServiceBlockingStub photoServiceStub;

  private final String firstUserId = "22222222-2222-2222-2222-222222222222";
  private final String secondUserId = "55555555-5555-5555-5555-555555555555";
  private final String anotherUserId = "33333333-3333-3333-3333-333333333333";

  @Sql(scripts = "/statsPhotosData.sql")
  @Test
  void shouldReturnCorrectPhotoCountsByCountry() {
    UsersStatRequest request = UsersStatRequest.newBuilder()
        .addAllUserIds(List.of(firstUserId, secondUserId, anotherUserId))
        .build();

    StatsResponse response = photoServiceStub.getUsersStatistics(request);

    assertThat(response.getStatsCount()).isEqualTo(2);

    assertThat(response.getStatsList())
        .anySatisfy(stat -> {
          assertThat(stat.getCountryCode()).isEqualTo("es");
          assertThat(stat.getCount()).isEqualTo(1);
        });

    assertThat(response.getStatsList())
        .anySatisfy(stat -> {
          assertThat(stat.getCountryCode()).isEqualTo("it");
          assertThat(stat.getCount()).isEqualTo(2);
        });
  }

  @Test
  void shouldReturnEmptyStatsWhenUserListIsEmpty() {
    UsersStatRequest request = UsersStatRequest.newBuilder().build();

    StatsResponse response = photoServiceStub.getUsersStatistics(request);

    assertThat(response.getStatsCount()).isZero();
  }

  @Test
  void shouldFailWhenUserIdIsInvalidUuid() {
    UsersStatRequest request = UsersStatRequest.newBuilder()
        .addUserIds("not-a-uuid")
        .build();

    assertThatThrownBy(() -> photoServiceStub.getUsersStatistics(request))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Sql(scripts = "/statsPhotosData.sql")
  @Test
  void shouldReturnEmptyStatsForUsersWithoutPhotos() {
    UsersStatRequest request = UsersStatRequest.newBuilder()
        .addUserIds(anotherUserId)
        .build();

    StatsResponse response = photoServiceStub.getUsersStatistics(request);

    assertThat(response.getStatsCount()).isZero();
  }
}
