package guru.qa.rangiffler.grpc.config;

import guru.qa.rangiffler.PhotoServiceGrpc;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.grpc.client.GrpcChannelFactory;

@TestConfiguration
public class GrpcTestStubsConfig {

  @Bean
  PhotoServiceGrpc.PhotoServiceBlockingStub photoServiceStub(GrpcChannelFactory channelFactory) {
    return PhotoServiceGrpc.newBlockingStub(channelFactory.createChannel("photo"));
  }
}

