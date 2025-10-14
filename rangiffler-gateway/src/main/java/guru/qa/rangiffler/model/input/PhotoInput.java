package guru.qa.rangiffler.model.input;

import guru.qa.rangiffler.model.type.CountryGql;

public record PhotoInput(
    String id,
    String src,
    CountryGql country,
    String description,
    LikeInput like
) {
}
