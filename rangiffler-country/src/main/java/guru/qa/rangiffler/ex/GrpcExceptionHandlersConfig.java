package guru.qa.rangiffler.ex;

import io.grpc.Status;
import io.grpc.StatusException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GrpcExceptionHandlersConfig implements GrpcExceptionHandler {

  @Override
  public StatusException handleException(Throwable exception) {
    return switch (exception) {
      case IllegalArgumentException e -> Status.INVALID_ARGUMENT
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      case CountryNotFoundException e -> Status.NOT_FOUND
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      default -> Status.INTERNAL
          .withDescription("Internal server error")
          .withCause(exception)
          .asException();
    };
  }
}