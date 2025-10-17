package guru.qa.rangiffler.model;

import guru.qa.rangiffler.defs.Country;

public record PhotoJson(
    String id,
    String src,
    Country country,
    String description) {
}
