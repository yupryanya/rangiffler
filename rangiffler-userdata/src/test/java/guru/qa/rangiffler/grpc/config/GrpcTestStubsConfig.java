package guru.qa.rangiffler.grpc.config;

import guru.qa.rangiffler.FriendshipServiceGrpc;
import guru.qa.rangiffler.UserDataServiceGrpc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;

@TestConfiguration
public class GrpcTestStubsConfig {
  @Bean
  public UserDataServiceGrpc.UserDataServiceBlockingStub userdataServiceStub(GrpcChannelFactory channelFactory) {
    return UserDataServiceGrpc.newBlockingStub(channelFactory.createChannel("userdata"));
  }

  @Bean
  public FriendshipServiceGrpc.FriendshipServiceBlockingStub friendshipServiceStub(GrpcChannelFactory channelFactory) {
    return FriendshipServiceGrpc.newBlockingStub(channelFactory.createChannel("friendship"));
  }
}
