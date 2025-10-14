package guru.qa.rangiffler.service.country;

import guru.qa.rangiffler.*;
import io.grpc.StatusRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
class CountryGrpcClient {
  private final CountryServiceGrpc.CountryServiceBlockingStub stub;

  @Autowired
  public CountryGrpcClient(CountryServiceGrpc.CountryServiceBlockingStub stub) {
    this.stub = stub;
  }

  public AllCountriesResponse getAllCountries(AllCountriesRequest request) {
    try {
      return stub.allCountries(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server (allCountries)", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }

  public CountryResponse getCountryByCode(CodeRequest request) {
    try {
      return stub.countryByCode(request);
    } catch (StatusRuntimeException e) {
      log.error("### Error while calling gRPC server (countryByCode)", e);
      throw new RuntimeException("The gRPC operation was cancelled", e);
    }
  }
}