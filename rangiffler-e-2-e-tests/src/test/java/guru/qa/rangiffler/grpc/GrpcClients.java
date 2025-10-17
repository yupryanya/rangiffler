package guru.qa.rangiffler.grpc;

import guru.qa.rangiffler.CountryServiceGrpc;
import guru.qa.rangiffler.FriendshipServiceGrpc;
import guru.qa.rangiffler.PhotoServiceGrpc;
import guru.qa.rangiffler.UserDataServiceGrpc;
import guru.qa.rangiffler.config.Config;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.qameta.allure.grpc.AllureGrpc;

public final class GrpcClients {

  private static final Config CFG = Config.getInstance();
  private static final GrpcConsoleInterceptor consoleInterceptor = new GrpcConsoleInterceptor();

  private static final ManagedChannel countryChannel = ManagedChannelBuilder
      .forAddress(CFG.countryGrpcAddress(), CFG.countryGrpcPort())
      .intercept(new AllureGrpc())
      .intercept(consoleInterceptor)
      .usePlaintext()
      .build();

  private static final ManagedChannel photoChannel = ManagedChannelBuilder
      .forAddress(CFG.photoGrpcAddress(), CFG.photoGrpcPort())
      .intercept(new AllureGrpc())
      .intercept(consoleInterceptor)
      .usePlaintext()
      .build();

  private static final ManagedChannel userdataChannel = ManagedChannelBuilder
      .forAddress(CFG.userdataGrpcAddress(), CFG.userdataGrpcPort())
      .intercept(new AllureGrpc())
      .intercept(consoleInterceptor)
      .usePlaintext()
      .build();

  private static final ManagedChannel friendshipChannel = ManagedChannelBuilder
      .forAddress(CFG.friendshipGrpcAddress(), CFG.friendshipGrpcPort())
      .intercept(new AllureGrpc())
      .intercept(consoleInterceptor)
      .usePlaintext()
      .build();

  public static final CountryServiceGrpc.CountryServiceBlockingStub countryService =
      CountryServiceGrpc.newBlockingStub(countryChannel);

  public static final PhotoServiceGrpc.PhotoServiceBlockingStub photoService =
      PhotoServiceGrpc.newBlockingStub(photoChannel);

  public static final UserDataServiceGrpc.UserDataServiceBlockingStub userdataService =
      UserDataServiceGrpc.newBlockingStub(userdataChannel);

  public static final FriendshipServiceGrpc.FriendshipServiceBlockingStub friendshipService =
      FriendshipServiceGrpc.newBlockingStub(friendshipChannel);

  private GrpcClients() {
  }
}