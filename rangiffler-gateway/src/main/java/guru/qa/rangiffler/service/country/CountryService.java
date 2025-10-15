package guru.qa.rangiffler.service.country;

import guru.qa.rangiffler.AllCountriesRequest;
import guru.qa.rangiffler.CodeRequest;
import guru.qa.rangiffler.model.type.CountryGql;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CountryService {
  private final CountryGrpcClient countryClient;

  @Autowired
  public CountryService(CountryGrpcClient countryClient) {
    this.countryClient = countryClient;
  }

  public List<CountryGql> getAllCountries() {
    log.info("Fetching all countries sorted by name ASC");
    AllCountriesRequest request = AllCountriesRequest.newBuilder()
        .setSortBy("name")
        .setDirection("ASC")
        .build();

    return countryClient.getAllCountries(request)
        .getCountriesList()
        .stream()
        .map(CountryGql::fromGrpcMessage)
        .toList();
  }

  public CountryGql getCountryByCode(String code) {
    log.info("Fetching country by code '{}'", code);
    CodeRequest request = CodeRequest.newBuilder()
        .setCode(code)
        .build();

    return CountryGql.fromGrpcMessage(
        countryClient.getCountryByCode(request)
    );
  }
}