package guru.qa.rangiffler.service;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.data.PhotoEntity;
import guru.qa.rangiffler.data.PhotoRepository;
import guru.qa.rangiffler.ex.PhotoNotFoundException;
import guru.qa.rangiffler.model.PhotoStat;
import guru.qa.rangiffler.service.mapper.PhotoMapper;
import guru.qa.rangiffler.utils.ImageResizer;
import io.grpc.stub.StreamObserver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoGrpcService extends PhotoServiceGrpc.PhotoServiceImplBase {
  private final PhotoRepository photoRepository;
  private final PhotoMapper photoMapper;
  private final ImageResizer imageResizer;

  @Autowired
  public PhotoGrpcService(PhotoRepository photoRepository, PhotoMapper photoMapper, ImageResizer imageResizer) {
    this.photoRepository = photoRepository;
    this.photoMapper = photoMapper;
    this.imageResizer = imageResizer;
  }

  @Override
  @Transactional(readOnly = true)
  public void getUsersPhotos(UsersPhotoRequest request, StreamObserver<PhotoListResponse> responseObserver) {
    if (request.getSize() <= 0 || request.getPage() < 0) {
      throw new IllegalArgumentException("Page size must be positive and page index non-negative");
    }

    List<UUID> userIds;
    try {
      userIds = request.getUserIdsList().stream()
          .map(UUID::fromString)
          .toList();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("One or more userIds are not valid UUIDs");
    }

    Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
    Page<PhotoEntity> photosPage = photoRepository.findByUserIdIn(userIds, pageable);

    PhotoListResponse response = PhotoListResponse.newBuilder()
        .addAllPhotos(photosPage.getContent().stream().map(photoMapper::toResponse).toList())
        .setTotal((int) photosPage.getTotalElements())
        .setPage(request.getPage())
        .setSize(request.getSize())
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Transactional
  public void addPhoto(PhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    if (request.getUserId().isBlank()) {
      throw new IllegalArgumentException("User id is required");
    }
    if (request.getPhoto().isEmpty()) {
      throw new IllegalArgumentException("Photo data is required");
    }

    PhotoEntity photo = new PhotoEntity();
    photo.setUserId(UUID.fromString(request.getUserId()));
    photo.setCountryCode(request.getCountryCode());
    photo.setDescription(request.getDescription());
    photo.setPhoto(imageResizer.resizeToHeight(request.getPhoto(), 1000));
    photo.setCreatedDate(new Date());

    PhotoEntity saved = photoRepository.save(photo);

    responseObserver.onNext(photoMapper.toResponse(saved));
    responseObserver.onCompleted();
  }

  @Transactional
  public void updatePhoto(UpdatePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    if (request.getId().isBlank()) {
      throw new IllegalArgumentException("Photo id is required");
    }

    UUID photoId = UUID.fromString(request.getId());
    PhotoEntity photo = photoRepository.findById(photoId)
        .orElseThrow(() -> new PhotoNotFoundException("Photo not found: " + photoId));

    if (!request.getCountryCode().isBlank()) {
      photo.setCountryCode(request.getCountryCode());
    }

    if (request.hasDescription()) {
      photo.setDescription(request.getDescription());
    }

    if (!request.getPhoto().isEmpty()) {
      photo.setPhoto(imageResizer.resizeToHeight(request.getPhoto(), 1000));
    }

    PhotoEntity updated = photoRepository.save(photo);

    responseObserver.onNext(photoMapper.toResponse(updated));
    responseObserver.onCompleted();
  }

  @Override
  @Transactional
  public void deletePhoto(DeletePhotoRequest request, StreamObserver<DeletePhotoResponse> responseObserver) {
    if (request.getId().isBlank()) {
      throw new IllegalArgumentException("Photo id is required");
    }

    UUID photoId = UUID.fromString(request.getId());
    PhotoEntity photo = photoRepository.findById(photoId)
        .orElseThrow(() -> new PhotoNotFoundException("Photo not found: " + photoId));

    photoRepository.delete(photo);

    DeletePhotoResponse response = DeletePhotoResponse.newBuilder()
        .setSuccess(true)
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();
  }

  @Override
  @Transactional
  public void likePhoto(LikePhotoRequest request, StreamObserver<PhotoResponse> responseObserver) {
    if (request.getPhotoId().isBlank() || request.getUserId().isBlank()) {
      throw new IllegalArgumentException("Photo id and User id are required");
    }

    UUID photoId;
    UUID userId;
    try {
      photoId = UUID.fromString(request.getPhotoId());
      userId = UUID.fromString(request.getUserId());
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("Invalid UUID format");
    }

    PhotoEntity photo = photoRepository.findById(photoId)
        .orElseThrow(() -> new PhotoNotFoundException("Photo not found: " + photoId));

     if (!photo.getLikedUserIds().contains(userId)) {
      photo.getLikedUserIds().add(userId);
      photoRepository.save(photo);
    }

    responseObserver.onNext(photoMapper.toResponse(photo));
    responseObserver.onCompleted();
  }

  @Override
  @Transactional(readOnly = true)
  public void getUsersStatistics(UsersStatRequest request, StreamObserver<StatsResponse> responseObserver) {
    List<UUID> userIds;
    try {
      userIds = request.getUserIdsList().stream()
          .map(UUID::fromString)
          .toList();
    } catch (IllegalArgumentException e) {
      throw new IllegalArgumentException("One or more userIds are not valid UUIDs");
    }

    List<PhotoStat> stats = photoRepository.findPhotoCountByUsersGroupedByCountry(userIds);

    StatsResponse.Builder responseBuilder = StatsResponse.newBuilder();
    stats.stream()
        .filter(stat -> stat.getCount() != null && stat.getCount() > 0)
        .forEach(stat -> responseBuilder.addStats(
            StatResponse.newBuilder()
                .setCountryCode(stat.getCountry())
                .setCount(stat.getCount().intValue())
                .build()
        ));

    responseObserver.onNext(responseBuilder.build());
    responseObserver.onCompleted();
  }
}