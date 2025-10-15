package guru.qa.rangiffler.service.photo;

import guru.qa.rangiffler.PhotoListResponse;
import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.StatsResponse;
import guru.qa.rangiffler.model.input.PhotoInput;
import guru.qa.rangiffler.model.mapper.PhotoGqlMapper;
import guru.qa.rangiffler.model.type.PhotoGql;
import guru.qa.rangiffler.model.type.StatGql;
import guru.qa.rangiffler.service.country.CountryService;
import guru.qa.rangiffler.service.user.UserService;
import jakarta.annotation.Nonnull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class PhotoService {
  private final PhotoGrpcClient photoClient;
  private final UserService userService;
  private final CountryService countryService;
  private final PhotoGqlMapper mapper;

  @Autowired
  public PhotoService(PhotoGrpcClient photoClient,
                      UserService userService,
                      CountryService countryService,
                      PhotoGqlMapper mapper) {
    this.photoClient = photoClient;
    this.userService = userService;
    this.countryService = countryService;
    this.mapper = mapper;
  }

  public Page<PhotoGql> getUsersPhotos(List<UUID> userIds, int page, int size) {
    PhotoListResponse response = photoClient.getUserPhotos(userIds, page, size);

    List<PhotoGql> photos = response.getPhotosList().stream()
        .map(photo -> mapper.fromPhotoResponse(photo, countryService.getCountryByCode(photo.getCountryCode())))
        .toList();

    return new PageImpl<>(photos, PageRequest.of(page, size), response.getTotal());
  }

  public @Nonnull PhotoGql savePhoto(@Nonnull Jwt principal, @Nonnull PhotoInput input) {
    final String username = principal.getClaim("sub");
    final UUID userId = userService.getUserId(username);

    final String photoIdStr = input.id();
    final boolean isLikeAction = input.like() != null && input.like().user() != null;

    PhotoResponse photoResponse;

    if (isLikeAction) {
      if (photoIdStr == null) {
        throw new IllegalArgumentException("Photo ID must be provided for like action");
      }
      final UUID photoId = UUID.fromString(photoIdStr);
      if (isOwner(userId, photoId)) {
        log.warn("User '{}' ({}) tried to like own photo '{}'", username, userId, photoId);
        throw new SecurityException("You don't have permission to like this photo");
      }
      log.info("User '{}' ({}) likes photo '{}'", username, userId, photoId);
      photoResponse = photoClient.likePhoto(userId, photoId);

    } else if (photoIdStr != null) {
      final UUID photoId = UUID.fromString(photoIdStr);
      if (!isOwner(userId, photoId)) {
        log.warn("User '{}' ({}) tried to edit someone else's photo '{}'", username, userId, photoId);
        throw new SecurityException("You don't have permission to modify this photo");
      }

      log.info("User '{}' ({}) updates photo '{}'", username, userId, photoIdStr);
      photoResponse = photoClient.updatePhoto(input);

    } else {
      log.info("User '{}' ({}) adds new photo", username, userId);
      photoResponse = photoClient.addPhoto(userId, input);
    }

    final var country = countryService.getCountryByCode(photoResponse.getCountryCode());
    return mapper.fromPhotoResponse(photoResponse, country);
  }

  public boolean deletePhoto(Jwt principal, UUID photoId) {
    String username = principal.getClaim("sub");
    UUID userId = userService.getUserId(username);
    if (!isOwner(userId, photoId)) {
      log.warn("User '{}' ({}) tried to delete someone else's photo '{}'", username, userId, photoId);
      throw new SecurityException("You don't have permission to delete this photo");
    }
    return photoClient.deletePhoto(photoId).getSuccess();
  }

  public List<StatGql> getUserStats(List<UUID> userIds) {
    StatsResponse response = photoClient.getUsersStat(userIds);
    return response.getStatsList().stream()
        .map(stat -> new StatGql(stat.getCount(), countryService.getCountryByCode(stat.getCountryCode())))
        .toList();
  }

  private boolean isOwner(@Nonnull UUID userId, @Nonnull UUID photoId) {
    PhotoResponse photo = photoClient.getPhoto(photoId);
    return photo.getUserId().equals(userId.toString());
  }
}