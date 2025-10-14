package guru.qa.rangiffler.config;

import guru.qa.rangiffler.CountryServiceGrpc;
import guru.qa.rangiffler.FriendshipServiceGrpc;
import guru.qa.rangiffler.PhotoServiceGrpc;
import guru.qa.rangiffler.UserDataServiceGrpc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.grpc.client.GrpcChannelFactory;

@Configuration
public class GrpcClientConfig {

  @Bean
  public CountryServiceGrpc.CountryServiceBlockingStub countryServiceStub(GrpcChannelFactory channelFactory) {
    return CountryServiceGrpc.newBlockingStub(channelFactory.createChannel("country"));
  }

  @Bean
  public PhotoServiceGrpc.PhotoServiceBlockingStub photoServiceStub(GrpcChannelFactory channelFactory) {
    return PhotoServiceGrpc.newBlockingStub(channelFactory.createChannel("photo"));
  }

  @Bean
  public UserDataServiceGrpc.UserDataServiceBlockingStub userdataServiceStub(GrpcChannelFactory channelFactory) {
    return UserDataServiceGrpc.newBlockingStub(channelFactory.createChannel("userdata"));
  }

  @Bean
  public FriendshipServiceGrpc.FriendshipServiceBlockingStub friendshipServiceStub(GrpcChannelFactory channelFactory) {
    return FriendshipServiceGrpc.newBlockingStub(channelFactory.createChannel("friendship"));
  }
}