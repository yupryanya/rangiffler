package guru.qa.rangiffler.service.grpc;

import guru.qa.rangiffler.LikePhotoRequest;
import guru.qa.rangiffler.PhotoRequest;
import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.PhotoServiceGrpc;
import guru.qa.rangiffler.grpc.GrpcClients;
import guru.qa.rangiffler.model.PhotoJson;
import lombok.extern.slf4j.Slf4j;

@Slf4j
class PhotoGrpcClient {
  private final PhotoServiceGrpc.PhotoServiceBlockingStub stub = GrpcClients.photoService;

  PhotoResponse addPhoto(String userId, PhotoJson photo) {
    try {
      PhotoRequest request = PhotoRequest.newBuilder()
          .setPhoto(photo.src())
          .setCountryCode(photo.country().getCode())
          .setDescription(photo.description() == null ? "" : photo.description())
          .setUserId(userId)
          .build();
      return stub.addPhoto(request);
    } catch (io.grpc.StatusRuntimeException e) {
      log.error("### Error while calling gRPC server", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  PhotoResponse likePhoto(String userId, PhotoJson photo) {
    try {
      LikePhotoRequest request = LikePhotoRequest.newBuilder()
          .setUserId(userId)
          .setPhotoId(photo.id())
          .build();
      return stub.likePhoto(request);
    } catch (io.grpc.StatusRuntimeException e) {
      log.error("### Error while calling gRPC server", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}