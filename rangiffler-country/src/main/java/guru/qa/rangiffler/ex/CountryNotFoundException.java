package guru.qa.rangiffler.ex;

public class CountryNotFoundException extends RuntimeException {
  public CountryNotFoundException(String message) {
    super(message);
  }
}
