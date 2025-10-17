package guru.qa.rangiffler.config;

enum LocalConfig implements Config {
  instance;

  @Override
  public String frontUrl() {
    return "http://127.0.0.1:3001/";
  }

  @Override
  public String authUrl() {
    return "http://127.0.0.1:9000/";
  }

  @Override
  public String gatewayUrl() {
    return "http://127.0.0.1:8090/";
  }

  @Override
  public String userdataUrl() {
    return "http://127.0.0.1:8089/";
  }

  @Override
  public String authJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3306/rangiffler-auth";
  }

  @Override
  public String userdataJdbcUrl() {
    return "jdbc:mysql://127.0.0.1:3309/rangiffler-userdata";
  }

  @Override
  public String countryGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public String photoGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public String userdataGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public String friendshipGrpcAddress() {
    return "127.0.0.1";
  }

  @Override
  public String kafkaAddress() {
    return "127.0.0.1:9092";
  }
}
