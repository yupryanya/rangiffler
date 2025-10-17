package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.defs.Country;
import guru.qa.rangiffler.jupiter.annotation.Photo;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.model.PhotoJson;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.service.db.UserCreationDbService;
import guru.qa.rangiffler.service.grpc.PhotoService;
import guru.qa.rangiffler.utils.ImageUtils;
import org.junit.jupiter.api.extension.*;
import org.junit.platform.commons.support.AnnotationSupport;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static guru.qa.rangiffler.utils.RandomDataUtils.*;

public class PhotoExtension implements
    ParameterResolver,
    BeforeEachCallback {

  private final UserCreationDbService userCreationService = new UserCreationDbService();
  public static final ExtensionContext.Namespace NAMESPACE = ExtensionContext.Namespace.create(PhotoExtension.class);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    AnnotationSupport.findAnnotation(context.getRequiredTestMethod(), User.class)
        .ifPresent(user -> {
          final PhotoService photoService = new PhotoService();

          UserJson contextUser = UserExtension.getContextUser();
          final String username = contextUser != null ? contextUser.username() : user.username();
          final List<PhotoJson> photos = new ArrayList<>();

          if (user.photos() != null) {
            for (Photo photo : user.photos()) {
              PhotoJson photoCreated = photoService.addPhoto(
                  username,
                  new PhotoJson(
                      null,
                      ImageUtils.toBase64FromClasspath(photo.src()),
                      photo.country(),
                      photo.description()
                  ));
              if (photo.likes() > 0) {
                for (int i = 1; i <= photo.likes(); i++) {
                  final UserJson createdUser = userCreationService.createUser(nonExistentUserName(), newValidPassword());
                  photoService.likePhoto(createdUser.username(), photoCreated);
                }
              }
              photos.add(photoCreated);
            }
          }
          if (user.randomPhoto() > 0) {
            for (int i = 1; i <= user.randomPhoto(); i++) {
              String src = "img/default-image.jpg";
              Country randomCountry = Country.values()[ThreadLocalRandom.current().nextInt(Country.values().length)];
              String randomDesc = randomString(25);

              PhotoJson randomPhoto = photoService.addPhoto(
                  username,
                  new PhotoJson(
                      null,
                      ImageUtils.toBase64FromClasspath(src),
                      randomCountry,
                      randomDesc
                  )
              );
              photos.add(randomPhoto);
            }
          }
          if (contextUser != null) {
            contextUser.testData().photos().addAll(photos);
          } else {
            context.getStore(NAMESPACE).put(context.getUniqueId(), photos);
          }
        });
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return false;
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) throws ParameterResolutionException {
    return null;
  }
}