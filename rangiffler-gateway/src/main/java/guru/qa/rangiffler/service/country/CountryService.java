package guru.qa.rangiffler.service.country;

import guru.qa.rangiffler.AllCountriesRequest;
import guru.qa.rangiffler.CodeRequest;
import guru.qa.rangiffler.model.type.CountryGql;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
  private final CountryGrpcClient countryClient;

  @Autowired
  public CountryService(CountryGrpcClient countryClient) {
    this.countryClient = countryClient;
  }

  public List<CountryGql> getAllCountries() {
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
    CodeRequest request = CodeRequest.newBuilder()
        .setCode(code)
        .build();

    return CountryGql.fromGrpcMessage(
        countryClient.getCountryByCode(request)
    );
  }
}
