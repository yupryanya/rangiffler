package guru.qa.rangiffler.controller.mutation;

import guru.qa.rangiffler.model.input.PhotoInput;
import guru.qa.rangiffler.model.type.PhotoGql;
import guru.qa.rangiffler.service.photo.PhotoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

import java.util.UUID;

@Slf4j
@Controller
@PreAuthorize("isAuthenticated()")
public class PhotoMutationController {
  private final PhotoService photoService;

  @Autowired
  public PhotoMutationController(PhotoService photoService) {
    this.photoService = photoService;
  }

  @MutationMapping
  public PhotoGql photo(@AuthenticationPrincipal Jwt principal,
                        @Argument PhotoInput input) {
    log.info("### Adding a new photo for user");

    return photoService.savePhoto(principal, input);
  }

  @MutationMapping
  public boolean deletePhoto(@AuthenticationPrincipal Jwt principal,
                             @Argument("id") String photoId) {
    log.info("### Deleting a photo with id: {}", photoId);

    return photoService.deletePhoto(principal, UUID.fromString(photoId));
  }
}
