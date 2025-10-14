package guru.qa.rangiffler.service.photo;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.model.input.PhotoInput;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Slf4j
@Component
class PhotoGrpcClient {
  private final PhotoServiceGrpc.PhotoServiceBlockingStub stub;

  @Autowired
  public PhotoGrpcClient(PhotoServiceGrpc.PhotoServiceBlockingStub stub) {
    this.stub = stub;
  }

  public PhotoListResponse getUserPhotos(List<UUID> userIds, int page, int size) {
    try {
      UsersPhotoRequest request = UsersPhotoRequest.newBuilder()
          .addAllUserIds(userIds.stream().map(UUID::toString).toList())
          .setPage(page)
          .setSize(size)
          .build();
      return stub.getUsersPhotos(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error getUserPhotos", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public PhotoResponse addPhoto(UUID userId, PhotoInput input) {
    try {
      PhotoRequest request = PhotoRequest.newBuilder()
          .setPhoto(input.src())
          .setUserId(userId.toString())
          .setCountryCode(input.country().code())
          .setDescription(input.description())
          .build();

      return stub.addPhoto(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error savePhoto", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public PhotoResponse updatePhoto(PhotoInput input) {
    try {
      UpdatePhotoRequest request = UpdatePhotoRequest.newBuilder()
          .setId(input.id())
          .setPhoto(input.src())
          .setCountryCode(input.country().code())
          .setDescription(input.description())
          .build();

      return stub.updatePhoto(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error savePhoto", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public DeletePhotoResponse deletePhoto(UUID photoId) {
    try {
      DeletePhotoRequest request = DeletePhotoRequest.newBuilder()
          .setId(photoId.toString())
          .build();
      return stub.deletePhoto(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error deletePhoto", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public StatsResponse getUsersStat(List<UUID> userIds) {
    try {
      UsersStatRequest request = UsersStatRequest.newBuilder()
          .addAllUserIds(userIds.stream().map(UUID::toString).toList())
          .build();
      return stub.getUsersStatistics(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error getUserStats", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public PhotoResponse likePhoto(UUID userId, UUID photoId) {
    try {
      LikePhotoRequest request = LikePhotoRequest.newBuilder()
          .setUserId(userId.toString())
          .setPhotoId(photoId.toString())
          .build();
      return stub.likePhoto(request);
    } catch (StatusRuntimeException e) {
      log.error("gRPC error likePhoto", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}