package guru.qa.rangiffler.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.annotation.Nonnull;
import java.util.UUID;

public record UserJson(
    @JsonProperty("id")
    UUID id,
    @JsonProperty("username")
    String username,
    @JsonProperty("firstname")
    String firstname,
    @JsonProperty("surname")
    String surname,
    @JsonProperty("avatar")
    String avatar,
    @JsonProperty("countryCode")
    String countryCode,
    @JsonIgnore
    TestData testData
) {
  public @Nonnull
  static UserJson generateUserJson(String username, String password) {
    return new UserJson(
        null,
        username,
        null,
        null,
        null,
        null,
        TestData.emptyTestData().withPassword(password)
    );
  }

  public @Nonnull UserJson withPassword(String password) {
    return new UserJson(
        this.id,
        this.username,
        this.firstname,
        this.surname,
        this.avatar,
        this.countryCode,
        this.testData.withPassword(password)
    );
  }

  public @Nonnull UserJson withTestData(TestData testData) {
    return new UserJson(
        this.id,
        this.username,
        this.firstname,
        this.surname,
        this.avatar,
        this.countryCode,
        testData
    );
  }

  public @Nonnull UserJson withEmptyTestData() {
    return new UserJson(
        this.id,
        this.username,
        this.firstname,
        this.surname,
        this.avatar,
        this.countryCode,
        TestData.emptyTestData()
    );
  }
}