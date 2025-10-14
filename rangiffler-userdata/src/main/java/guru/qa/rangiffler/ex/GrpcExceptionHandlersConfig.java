package guru.qa.rangiffler.ex;

import io.grpc.Status;
import io.grpc.StatusException;
import org.springframework.grpc.server.exception.GrpcExceptionHandler;
import org.springframework.stereotype.Component;

@Component
public class GrpcExceptionHandlersConfig implements GrpcExceptionHandler {

  @Override
  public StatusException handleException(Throwable exception) {
    return switch (exception) {
      case IllegalArgumentException e -> Status.INVALID_ARGUMENT
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      case UserNotFoundException e -> Status.NOT_FOUND
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      case IllegalStateException e -> Status.FAILED_PRECONDITION
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      case UnsupportedOperationException e -> Status.UNIMPLEMENTED
          .withDescription(e.getMessage())
          .withCause(exception)
          .asException();

      case SelfFriendshipException e -> Status.FAILED_PRECONDITION
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