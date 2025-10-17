package guru.qa.rangiffler.config;

import javax.annotation.Nonnull;

enum DockerConfig implements Config {
  instance;

  @Nonnull
  @Override
  public String frontUrl() {
    return "http://frontend.rangiffler.dc/";
  }

  @Nonnull
  @Override
  public String authUrl() {
    return "http://auth.rangiffler.dc:9000/";
  }

  @Nonnull
  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://rangiffler-auth-db:3306/rangiffler-auth";
  }

  @Nonnull
  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://rangiffler-userdata-db:3306/rangiffler-userdata";
  }

  @Nonnull
  @Override
  public String gatewayUrl() {
    return "http://gateway.rangiffler.dc:8090/";
  }

  @Nonnull
  @Override
  public String userdataUrl() {
    return "http://userdata.rangiffler.dc:8089/";
  }

  @Override
  public String countryGrpcAddress() {
    return "country.rangiffler.dc";
  }

  @Override
  public String photoGrpcAddress() {
    return "photo.rangiffler.dc";
  }

  @Override
  public String userdataGrpcAddress() {
    return "userdata.rangiffler.dc";
  }

  @Override
  public String friendshipGrpcAddress() {
    return "userdata.rangiffler.dc";
  }

  @Override
  public String kafkaAddress() {
    return "kafka:9092";
  }
}