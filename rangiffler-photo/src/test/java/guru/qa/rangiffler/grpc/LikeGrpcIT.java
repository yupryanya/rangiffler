package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.LikePhotoRequest;
import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.PhotoServiceGrpc;
import guru.qa.rangiffler.grpc.config.GrpcTestStubsConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Import(GrpcTestStubsConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class LikeGrpcIT {
  private final String defaultPhotoId = "11111111-1111-1111-1111-111111111111";
  private final String defaultUserId = "22222222-2222-2222-2222-222222222222";
  private final String anotherUserId = "33333333-3333-3333-3333-333333333333";

  @Autowired
  PhotoServiceGrpc.PhotoServiceBlockingStub photoServiceStub;

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldAddLikeToExistingPhoto() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId(defaultPhotoId)
        .setUserId(anotherUserId)
        .setCreatedDate(com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(Instant.now().getEpochSecond())
            .build())
        .build();

    PhotoResponse response = photoServiceStub.likePhoto(likeRequest);

    assertThat(response.getId()).isEqualTo(defaultPhotoId);
    assertThat(response.getLikedUserIdsList()).contains(anotherUserId);
  }

  @Test
  void shouldFailToLikeNonExistentPhoto() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId("99999999-9999-9999-9999-999999999999")
        .setUserId(defaultUserId)
        .setCreatedDate(com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(Instant.now().getEpochSecond())
            .build())
        .build();

    assertThatThrownBy(() -> photoServiceStub.likePhoto(likeRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("NOT_FOUND");
  }

  @Test
  void shouldFailToLikePhotoWithEmptyPhotoId() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId("")
        .setUserId(defaultUserId)
        .setCreatedDate(com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(Instant.now().getEpochSecond())
            .build())
        .build();

    assertThatThrownBy(() -> photoServiceStub.likePhoto(likeRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Test
  void shouldFailToLikePhotoWithInvalidPhotoId() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId("invalid-uuid")
        .setUserId(defaultUserId)
        .setCreatedDate(com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(Instant.now().getEpochSecond())
            .build())
        .build();

    assertThatThrownBy(() -> photoServiceStub.likePhoto(likeRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldFailToLikePhotoWithInvalidUserId() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId(defaultPhotoId)
        .setUserId("invalid-uuid")
        .setCreatedDate(com.google.protobuf.Timestamp.newBuilder()
            .setSeconds(Instant.now().getEpochSecond())
            .build())
        .build();

    assertThatThrownBy(() -> photoServiceStub.likePhoto(likeRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }

  @Sql(scripts = "/existingPhotosData.sql")
  @Test
  void shouldFailToLikePhotoWithEmptyUserId() {
    LikePhotoRequest likeRequest = LikePhotoRequest.newBuilder()
        .setPhotoId(defaultPhotoId)
        .setUserId("")
        .build();

    assertThatThrownBy(() -> photoServiceStub.likePhoto(likeRequest))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("INVALID_ARGUMENT");
  }
}
