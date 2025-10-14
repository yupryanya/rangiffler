package guru.qa.rangiffler.model.type;

import com.fasterxml.jackson.annotation.JsonProperty;
import guru.qa.rangiffler.CountryResponse;

public record CountryGql(
    @JsonProperty("code")
    String code,
    @JsonProperty("name")
    String name,
    @JsonProperty("flag")
    String flag) {

  public static CountryGql fromGrpcMessage(CountryResponse countryMessage) {
    return new CountryGql(
        countryMessage.getCode(),
        countryMessage.getName(),
        countryMessage.getFlag()
    );
  }
}
