package guru.qa.rangiffler.service;

import guru.qa.rangiffler.*;
import guru.qa.rangiffler.data.CountryEntity;
import guru.qa.rangiffler.data.CountryRepository;
import guru.qa.rangiffler.data.mapper.CountryMapper;
import guru.qa.rangiffler.ex.CountryNotFoundException;
import io.grpc.stub.StreamObserver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
public class CountryGrpcService extends CountryServiceGrpc.CountryServiceImplBase {

  private final CountryRepository countryRepository;
  private final CountryMapper mapper;

  @Autowired
  public CountryGrpcService(CountryRepository countryRepository, CountryMapper mapper) {
    this.countryRepository = countryRepository;
    this.mapper = mapper;
  }

  @Transactional(readOnly = true)
  @Override
  public void allCountries(AllCountriesRequest request, StreamObserver<AllCountriesResponse> responseObserver) {
    log.info("Received request for all countries with sortBy='{}', direction='{}'",
        request.getSortBy(), request.getDirection());

    Sort.Direction direction = "DESC".equalsIgnoreCase(request.getDirection())
        ? Sort.Direction.DESC : Sort.Direction.ASC;

    Set<String> allowedSortFields = Set.of("id", "name", "code");

    String sortBy = request.getSortBy().isBlank() ? "name" : request.getSortBy();
    if (!allowedSortFields.contains(sortBy)) {
      log.warn("Invalid sort field '{}', defaulting to 'name'", sortBy);
      sortBy = "name";
    }

    List<CountryEntity> countries = countryRepository.findAll(Sort.by(direction, sortBy));
    log.debug("Fetched {} countries sorted by '{}' ({})", countries.size(), sortBy, direction);

    AllCountriesResponse response = AllCountriesResponse.newBuilder()
        .addAllCountries(mapper.toGrpcResponseList(countries))
        .build();

    responseObserver.onNext(response);
    responseObserver.onCompleted();

    log.info("Successfully responded with {} countries", countries.size());
  }

  @Transactional(readOnly = true)
  @Override
  public void countryByCode(CodeRequest request, StreamObserver<CountryResponse> responseObserver) {
    String code = request.getCode();
    log.info("Received request for country by code '{}'", code);

    if (code.isBlank()) {
      log.error("Country code is empty");
      throw new IllegalArgumentException("Country code must not be empty");
    }

    CountryEntity country = countryRepository.findByCode(code)
        .orElseThrow(() -> {
          log.error("Country not found with code '{}'", code);
          return new CountryNotFoundException("Country not found with code: " + code);
        });

    log.debug("Found country: {} ({})", country.getName(), country.getCode());

    CountryResponse response = mapper.toGrpcResponse(country);
    responseObserver.onNext(response);
    responseObserver.onCompleted();

    log.info("Successfully responded with country '{}'", country.getName());
  }
}