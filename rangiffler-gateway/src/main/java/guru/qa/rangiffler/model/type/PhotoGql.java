package guru.qa.rangiffler.model.type;

import java.util.Date;
import java.util.UUID;

public record PhotoGql(
    UUID id,
    String src,
    CountryGql country,
    String description,
    Date creationDate,
    LikesGql likes
) {
}
