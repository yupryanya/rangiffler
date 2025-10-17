package guru.qa.rangiffler.utils;

import com.github.javafaker.Faker;

import java.util.Date;

import static java.util.concurrent.TimeUnit.DAYS;

public class RandomDataUtils {
  private static final Faker faker = new Faker();

  public static String nonExistentUserName() {
    return faker.name().username() + faker.number().digit();
  }

  public static String newValidPassword() {
    return faker.internet().password(8, 12, true, true);
  }

  public static String shortUsername() {
    return faker.name().username().substring(0, 2);
  }

  public static String longUsername() {
    return faker.lorem().characters(51);
  }

  public static String shortPassword() {
    return faker.internet().password(1, 2, true, true);
  }

  public static String longPassword() {
    return faker.internet().password(13, 20, true, true);
  }

  public static double randomAmount() {
    return faker.number().randomDouble(2, 0, 10000);
  }

  public static Date randomDate() {
    return faker.date().past(1000, DAYS, new Date());
  }

  public static String randomString(int length) {
    return faker.lorem().characters(length);
  }
}
