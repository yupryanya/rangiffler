package guru.qa.rangiffler.ex;

import graphql.GraphQLError;
import graphql.GraphqlErrorBuilder;
import graphql.schema.DataFetchingEnvironment;
import org.springframework.graphql.execution.DataFetcherExceptionResolverAdapter;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class GraphqlExceptionHandlersConfig extends DataFetcherExceptionResolverAdapter {

  protected GraphQLError resolveToSingleError(Throwable ex, DataFetchingEnvironment env) {
    return switch (ex) {
      case IllegalArgumentException e -> GraphqlErrorBuilder.newError()
          .message(e.getMessage())
          .errorType(ErrorType.BAD_REQUEST)
          .build();

      case AccessDeniedException e -> GraphqlErrorBuilder.newError()
          .message("Unauthorized - " + e.getMessage())
          .errorType(ErrorType.UNAUTHORIZED)
          .build();

      case NotFoundException e -> GraphqlErrorBuilder.newError()
          .message(e.getMessage())
          .errorType(ErrorType.NOT_FOUND)
          .build();

      default -> GraphqlErrorBuilder.newError()
          .message("Internal server error")
          .errorType(ErrorType.INTERNAL_ERROR)
          .build();
    };
  }
}
