package guru.qa.rangiffler.config;

import java.util.List;

public interface Config {

  static Config getInstance() {
    return "docker".equals(System.getProperty("test.env"))
        ? DockerConfig.instance
        : LocalConfig.instance;
  }

  String frontUrl();

  String authUrl();

  String authJdbcUrl();

  String gatewayUrl();

  String userdataUrl();

  String userdataJdbcUrl();

  String countryGrpcAddress();

  default int countryGrpcPort() {
    return 8092;
  }

  String photoGrpcAddress();

  default int photoGrpcPort() {
    return 8094;
  }

  String userdataGrpcAddress();

  default int userdataGrpcPort() {
    return 8096;
  }

  String friendshipGrpcAddress();

  default int friendshipGrpcPort() {
    return 8096;
  }

  default List<String> kafkaTopics() {
    return List.of("users");
  }

  String kafkaAddress();
}
