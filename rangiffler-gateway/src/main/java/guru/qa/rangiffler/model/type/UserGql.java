package guru.qa.rangiffler.model.type;

import guru.qa.rangiffler.model.FriendStatus;
import org.springframework.data.domain.Slice;

import java.util.UUID;

public record UserGql(
    UUID id,
    String username,
    String firstname,
    String surname,
    String avatar,
    FriendStatus friendStatus,
    Slice<UserGql> friends,
    Slice<UserGql> incomeInvitations,
    Slice<UserGql> outcomeInvitations,
    CountryGql location
) {
}
