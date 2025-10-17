package guru.qa.rangiffler.service.grpc;

import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.model.PhotoJson;

public class PhotoService {

  public PhotoJson addPhoto(String username, PhotoJson photo) {
    String userId = new UserGrpcClient().findUserByUsername(username).getId();
    PhotoResponse response = new PhotoGrpcClient().addPhoto(userId, photo);

    return new PhotoJson(
        response.getId(),
        response.getPhoto(),
        photo.country(),
        photo.description()
    );
  }

  public PhotoJson likePhoto(String username, PhotoJson photo) {
    String userId = new UserGrpcClient().findUserByUsername(username).getId();
    PhotoResponse response = new PhotoGrpcClient().likePhoto(userId, photo);

    return new PhotoJson(
        response.getId(),
        response.getPhoto(),
        photo.country(),
        photo.description()
    );
  }
}
