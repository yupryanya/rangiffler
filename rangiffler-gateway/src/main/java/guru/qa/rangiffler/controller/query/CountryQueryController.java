package guru.qa.rangiffler.controller.query;

import guru.qa.rangiffler.model.type.CountryGql;
import guru.qa.rangiffler.service.country.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.List;

@Slf4j
@Controller
@PreAuthorize("isAuthenticated()")
public class CountryQueryController {
  private final CountryService countryService;

  @Autowired
  public CountryQueryController(CountryService countryService) {
    this.countryService = countryService;
  }

  @QueryMapping
  public List<CountryGql> countries() {
    log.info("### Fetching all countries");

    return countryService.getAllCountries();
  }
}