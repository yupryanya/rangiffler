package guru.qa.rangiffler.model.mapper;

import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.model.type.CountryGql;
import guru.qa.rangiffler.model.type.LikeGql;
import guru.qa.rangiffler.model.type.LikesGql;
import guru.qa.rangiffler.model.type.PhotoGql;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
public class PhotoGqlMapper {
  public PhotoGql fromPhotoResponse(PhotoResponse photo, CountryGql location) {
    return new PhotoGql(
        UUID.fromString(photo.getId()),
        photo.getPhoto(),
        location,
        photo.getDescription(),
        new Date(),
        new LikesGql(
            photo.getLikedUserIdsList().size(),
            photo.getLikedUserIdsList().stream()
                .map(UUID::fromString)
                .map(LikeGql::new)
                .toList()
        )
    );
  }
}
