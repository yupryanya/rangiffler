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
import org.springframework.security.access.AccessDeniedException;
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
    UUID userId = userService.getUserId(principal.getClaim("sub"));

    if (input.id() == null) {
      log.info("Adding new photo for user {}", userId);
      PhotoResponse photoResponse = photoClient.addPhoto(userId, input);
      return mapper.fromPhotoResponse(photoResponse, countryService.getCountryByCode(photoResponse.getCountryCode()));
    }

    UUID photoId = UUID.fromString(input.id());
    boolean hasLikeAction = input.like() != null && input.like().user() != null;

    if (hasLikeAction) {
      if (isOwner(userId, photoId)) {
        log.warn("User {} tried to like own photo {}", userId, photoId);
        throw new AccessDeniedException("You don't have permission to like this photo");
      }
      log.info("User {} likes photo {}", userId, photoId);
      PhotoResponse photoResponse = photoClient.likePhoto(userId, photoId);
      return mapper.fromPhotoResponse(photoResponse, countryService.getCountryByCode(photoResponse.getCountryCode()));
    }

    if (!isOwner(userId, photoId)) {
      log.warn("User {} tried to update someone else's photo {}", userId, photoId);
      throw new AccessDeniedException("You don't have permission to update this photo");
    }

    log.info("User {} updates photo {}", userId, photoId);
    PhotoResponse photoResponse = photoClient.updatePhoto(input);
    return mapper.fromPhotoResponse(photoResponse, countryService.getCountryByCode(photoResponse.getCountryCode()));
  }

  public boolean deletePhoto(Jwt principal, UUID photoId) {
    UUID userId = userService.getUserId(principal.getClaim("sub"));
    if (!isOwner(userId, photoId)) {
      log.warn("User {} tried to delete someone else's photo {}", userId, photoId);
      throw new AccessDeniedException("You don't have permission to delete this photo");
    }
    log.info("User {} deleted photo {}", userId, photoId);
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