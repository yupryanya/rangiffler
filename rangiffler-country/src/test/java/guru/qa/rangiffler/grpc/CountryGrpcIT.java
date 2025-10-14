package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CountryGrpcIT {

  @TestConfiguration
  static class StubConfig {
    @Bean
    CountryServiceGrpc.CountryServiceBlockingStub countryServiceStub(GrpcChannelFactory channelFactory) {
      return CountryServiceGrpc.newBlockingStub(channelFactory.createChannel("country"));
    }
  }

  @Autowired
  CountryServiceGrpc.CountryServiceBlockingStub countryServiceStub;

  private record Country(String id, String code, String name, String flag) {
  }

  List<Country> expectedCountries = List.of(
      new Country(
          "11111111-1111-1111-1111-111111111111",
          "tg",
          "Togo",
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAABPlBMVEVHcEwDUD25GkIASDMAUjwBWUMAPisARjIAVT4AVzwAUTsCVkAATjkBXkYATTi4GkEBXUa9I0mjI0O4GkC6HEUBZk4AzUn/0AAAaEz/zQABZUkAa08AVDwAXEHZGkX/yAD/zADWFT3/wgAAblMAYEYAWD/VEjkAYkj/0QAAUTj/zwDaHkv/xQABXkPXF0EAc1cARTC7GEL/ygD///8AdlvovwO1FToASjXeP2LJG0ZbiS7GFj4AOyhNfS46by5bkDPhSC2OpCMrdTubKT6/lALTrQGmpRiwsRlxkSeGlyDHngD1xM4AQCzaLDDQvg7gqgDsxgXGtA96mijCrg3mcYvktQHskKWiMEmRjBItVyTdOysXdUnwywQjZzW7OC8/hz7iVHXxrr0zfT0NWTb98/X53uT3sQqgmBXuiRfcixJvgMLrAAAAFnRSTlMAIMLXrVy5i8gH7kJm1DCV6iR+r1HVEsrmUgAAAlFJREFUOMtt1HlfokAYwHHySLTStnY/0ggUCrJkosGS5Kpp3qu2Xtmudh97vP83sM8MA9rW7z/5fB2YZxSGgT583Int+ny+3dhOcGONeVs4yIKK+Y7dTuoch9CnQAh82EXRzUCE5xlQp6diJpMRcVdfcqlUglORKsvbW1ssu7WtQFmBZ45PMzhbpJCUy4FGSAWuqojjuKzmwocVmHNKkRKJBECBQvH+Gd9a16/eMuyQUCFQFGfm1LZtSa9eX7RrZ3eL1Cv32J18w/B+1ns2TfPF1vXqZ1Ic+/EZblwbDQ3jEkPR7gEzexldklwYPyTtQQcHhrEP8ARmA3c2TVuSKIzHHegw7Aj8Crt4MHvmjMI4dXvucuBcqL9M9WkPw9ta++LaW85z1o+fGOr2FB7P1qV0uo73urjrgB86tzWsSbec5QsAdV2USGkCE3QmHLc4h5AsZ7OClifQZQCXDCF8hipxFC4dgR4jZ02cQiBxg8F8Ph8M6g5bXQ47RSObqf75ZdCRHFij1rh4zlHmOOGm02gwt38PyeyWE4G97luTVrdTxhW7rctk8nuDaVr/O8OZHe4ISh4lXUjcO4w4zCj0HGWry1HnwmG71iniOt3WyHrHJeEIm5PxozM7dyTovIi9x5q/y0/5ElNB9NeOVkaCZ5K9qeBunnhN0wAq6opbMkEh8ZBGoQx/TFlwrvG8ogivGXGFPkC4Ch8ifpZl/ZGCk6bBV3jqCvl+qbTOKHwksBn13h9rG5uhgD/v1e+D8oeiDMMGw++8lbAPBdahUChIXlv/AKxstxK/9pV0AAAAAElFTkSuQmCC"
      ),
      new Country(
          "22222222-2222-2222-2222-222222222222",
          "la",
          "Lao People`s Democratic Republic",
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAAAz1BMVEVHcEyYExyiEyOiEienEyanEiuaChilDx+kDyC2GzKPBxKZChewFiulDh+jEyKpFSiSDCD////WHTjTGDEAIV3OECXQEyoALm7LDyMAM3MAJmXIDSABLGsAI2AAMHAAH1nBCxvEDB0AHVMAKWkAJGMBN3e+ChgCGEaqDR61DR6ZCRa4GjO8EiilCxmxFSqNBxHHHDdbHkoBKmB0GT0YMWuRGj07LWI6GUYYHFChr8c9WYj4+fojRn5ke6IYIlnX3ueAkrGKnbpUcp0iKGJuhqqjkXjNAAAAEXRSTlMAB0cfjfymyWK2udfY6i99/KGNB4kAAAHWSURBVDjLddTZdqowGEDhqIBYp1qHCEYFtShDBEThOGvb93+mJoFAaHv24vJLLn6SAECqVbtSgyZ11WoN/K6i1gHoSC23aEtqNSTiKxx1VEXGGDRcNwje8vpB4HlbVrtVr9db7Q3JtjFwRfXWTxsFY4+2tW3PM8fD4U8oNsoaksYi7AtulLPUleEfuzFG3Nh2KAwOYRzvaXEcRgd39MvJ4Rq4UfzO0mizmQYh/KRe9hjy5Ci8TedrsC+xGYQfz+PXBaElSaetVqvpdLEGMy2H1L3fe6TzE5XcgECoCQzCYy/tiAo2HVCINO4Ig5dzBntX0TE4Exz64K53XwpuzmDuEELXHPYuKWOOwh0UHDoV8CS4DOZsuUT3AmaMuvmaQMQdm9wXd2ckuEUKBafrfD6Pgs0XKWQu/xH6g03ouBsIjsFl5vQbmQedyP76fJymJcfgDn6GxQk4RGFyG/xkSeSDW+gWx4k1mZjMJ/9oSRK92hb2wcbjp5gzmmmSj2YYhmVYGRTchGfmjsgM/t8Z3HE4MWx7s0kvsGWYpe2YsxwCicEYt5vktjfbDg1jtoAzGzu+/wI2WFbUTv5+1KqqpDRfnSLf95tSB4C6WvnjVWJeeSEp3fTZ+ga4sIH1VJC/oQAAAABJRU5ErkJggg=="
      ),
      new Country(
          "33333333-3333-3333-3333-333333333333",
          "mr",
          "Mauritania",
          "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACgAAAAeCAMAAABpA6zvAAAA/1BMVEVHcEyeHByjHSCpHiGcEhSZEROiFRitHh+nHR+lFxmlGR2oGx24KC2jGx60JSm1JioArF8Aql0Ar2QAsWgAtG0AplgAqFoBn1PPGh0ArWIAnlAAmUvDExUBo1jMGBv/1QAAolTYLjLIFhi4FxoAnE7UJCjWKS3TICPRHB+bDxGsFRi6KCymEhW/ERMDlkiGTDQGhEK4ICOPwCaPDA5quzRQtT0Gl10AkFTHLTDs1AYFpmUAej5faUEAjUkAcjeYMyMlr02jSTtsXTkoiEwWpUvHIycxtVBAhlXVzQ44skbEzRSGXkRwMyO7PjgTrVesyB47YjKlOCt6RipDckBHfUxgs9ywAAAAEHRSTlMABx9Fwajn/IrSa1y8L6/YlLovBQAAAh5JREFUOMtt1HtbokAUgPGxQPAaVCqmhOtYuyKYIKSYbXjPe7X7/T/LnrmZtr3++/PMeRQGIejsPK3ms9lsXlUz52fo/xIZCaGcetESeS3P85SLPPEJgXJpWXEclG21+v0ftHvSbDYD6ymKkkylpFQqaUMYO6jFFXfQHYlwCMOnfmsYR/D+xEE/ab8g4wj2t6PRejTa9u++dQbeANyuF4Nr6Io0eFwM16P+KXsfdjroYXHNou7y6hIqlweTv8PxA2k8nJQKZq+DHgfHjrFyWStrkK4Xi8VCwTRrz08cnrAdQ5wRV+txSN2cMU0LAsqOXIlPZNv5zM1X0SqIKaOuBo5Dfqwf0nna0rL8HRsXmszdUCjWm0dzulywf12ycabPXUNAtt0eJKwW67uQnRoE3FFoCqfFkRXoh+UKtTCactfoPKGeyRw5NYys15AzsxBYPmHEVTq/AQoHw0Ba0T5YxsvQhy9NhRNQOL0Yr6xD1DWoo7BEnS6WCwX1p2JcpdKm8MjR5eLA9/3w81hwHFInGPnDaqUbzrhjUNNKi8Mz9WfSJj/JKWu33zboeTJ8Z4+nYdzSvJePMXjuYL+3D+x00cbjjzFnUP22DmHlhaTgatV1Adr4i6sfqvJcDsFUMbZt9gK71S+MOLdJILYdx+Fve5PkODZ23aNxzW5XQraTlNO5w/1BriE51fysCyXVHEJSJvHNrUS9LEmSLKsZem39A72bk4EaoeFoAAAAAElFTkSuQmCC"
      )
  );

  @Sql(scripts = "/countries.sql")
  @Test
  void shouldReturnAllCountriesInAscOrder() {
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setSortBy("name")
        .setDirection("ASC")
        .build();

    AllCountriesResponse response = countryServiceStub.allCountries(request);

    List<Country> actualCountries = response.getCountriesList().stream()
        .map(c -> new Country(c.getId(), c.getCode(), c.getName(), c.getFlag()))
        .toList();

    List<Country> sortedCountries = expectedCountries.stream()
        .sorted(Comparator.comparing(Country::name))
        .toList();

    assertThat(actualCountries)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(sortedCountries);
  }

  @Sql(scripts = "/countries.sql")
  @Test
  void shouldReturnAllCountriesInAscOrderIfNoDirectionPassed() {
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setSortBy("name")
        .build();

    AllCountriesResponse response = countryServiceStub.allCountries(request);

    List<Country> actualCountries = response.getCountriesList().stream()
        .map(c -> new Country(c.getId(), c.getCode(), c.getName(), c.getFlag()))
        .toList();

    List<Country> sortedCountries = expectedCountries.stream()
        .sorted(Comparator.comparing(Country::name))
        .toList();

    assertThat(actualCountries)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(sortedCountries);
  }

  @Sql(scripts = "/countries.sql")
  @Test
  void shouldReturnAllCountriesInDescOrder() {
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setSortBy("name")
        .setDirection("DESC")
        .build();

    AllCountriesResponse response = countryServiceStub.allCountries(request);

    List<Country> actualCountries = response.getCountriesList().stream()
        .map(c -> new Country(c.getId(), c.getCode(), c.getName(), c.getFlag()))
        .toList();

    List<Country> sortedCountries = expectedCountries.stream()
        .sorted(Comparator.comparing(Country::name).reversed())
        .toList();

    assertThat(actualCountries)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(sortedCountries);
  }

  @Sql(scripts = "/countries.sql")
  @Test
  void shouldReturnCountryByCode() {
    CodeRequest request = CodeRequest.newBuilder()
        .setCode("mr")
        .build();

    CountryResponse response = countryServiceStub.countryByCode(request);

    Country expectedCountry = expectedCountries.get(2);

    assertThat(response.getId()).isEqualTo(expectedCountry.id());
    assertThat(response.getName()).isEqualTo(expectedCountry.name());
    assertThat(response.getCode()).isEqualTo(expectedCountry.code());
    assertThat(response.getFlag()).isEqualTo(expectedCountry.flag());
  }

  @Test
  void shouldThrowWhenCountryNotFound() {
    CodeRequest request = CodeRequest.newBuilder()
        .setCode("xx")
        .build();

    assertThatThrownBy(() -> countryServiceStub.countryByCode(request))
        .isInstanceOf(io.grpc.StatusRuntimeException.class)
        .hasMessageContaining("NOT_FOUND");
  }

  @Sql(scripts = "/countries.sql")
  @Test
  void shouldSortAllCountriesByNameIfSortFieldNotFound() {
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setSortBy("unknown")
        .setDirection("ASC")
        .build();

    AllCountriesResponse response = countryServiceStub.allCountries(request);

    List<Country> actualCountries = response.getCountriesList().stream()
        .map(c -> new Country(c.getId(), c.getCode(), c.getName(), c.getFlag()))
        .toList();

    List<Country> sortedCountries = expectedCountries.stream()
        .sorted(Comparator.comparing(Country::name))
        .toList();

    assertThat(actualCountries)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactlyElementsOf(sortedCountries);
  }
}