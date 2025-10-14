package guru.qa.rangiffler.service.mapper;

import com.google.protobuf.util.Timestamps;
import guru.qa.rangiffler.PhotoResponse;
import guru.qa.rangiffler.data.PhotoEntity;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Component
public class PhotoMapper {

  public PhotoResponse toResponse(PhotoEntity photo) {
    return PhotoResponse.newBuilder()
        .setId(photo.getId().toString())
        .setUserId(photo.getUserId().toString())
        .setPhoto(new String(photo.getPhoto(), StandardCharsets.UTF_8))
        .setCountryCode(photo.getCountryCode())
        .setDescription(photo.getDescription())
        .setCreatedDate(Timestamps.fromDate(photo.getCreatedDate()))
        .addAllLikedUserIds(
            photo.getLikedUserIds().stream()
                .map(UUID::toString)
                .toList()
        )
        .build();
  }
}