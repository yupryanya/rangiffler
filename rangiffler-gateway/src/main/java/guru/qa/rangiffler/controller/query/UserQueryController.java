package guru.qa.rangiffler.controller.query;

import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.type.UserGql;
import guru.qa.rangiffler.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@PreAuthorize("isAuthenticated()")
public class UserQueryController {
  private final UserService userService;

  @Autowired
  public UserQueryController(UserService userService) {
    this.userService = userService;
  }

  @QueryMapping
  public UserGql user(@AuthenticationPrincipal Jwt principal) {
    String username = principal.getClaim("sub");
    log.info("### Fetching current user with username: {}", username);

    return userService.getUserByUsername(username);
  }

  @QueryMapping
  public Page<UserGql> users(@AuthenticationPrincipal Jwt principal,
                             @Argument int page,
                             @Argument int size,
                             @Argument String searchQuery) {
    String username = principal.getClaim("sub");
    log.info("### Fetching all users for user: {}, searchQuery: {}", username, searchQuery);

    return userService.getAllUsers(username, FriendStatus.NONE, PageRequest.of(page, size), searchQuery);
  }

  @SchemaMapping(typeName = "User", field = "friends")
  public Page<UserGql> friends(UserGql user,
                               @Argument int page,
                               @Argument int size,
                               @Argument String searchQuery) {
    String username = user.username();
    log.info("### Fetching all friends for user: {}, searchQuery: {}", username, searchQuery);

    return userService.getAllUsers(username, FriendStatus.FRIEND, PageRequest.of(page, size), searchQuery);
  }

  @SchemaMapping(typeName = "User", field = "incomeInvitations")
  public Page<UserGql> incomeInvitations(UserGql user,
                                         @Argument int page,
                                         @Argument int size,
                                         @Argument String searchQuery) {
    String username = user.username();
    log.info("### Fetching income requests for user: {}, searchQuery: {}", username, searchQuery);

    return userService.getAllUsers(username, FriendStatus.INVITATION_RECEIVED, PageRequest.of(page, size), searchQuery);
  }

  @SchemaMapping(typeName = "User", field = "outcomeInvitations")
  public Page<UserGql> outcomeInvitations(UserGql user,
                                          @Argument int page,
                                          @Argument int size,
                                          @Argument String searchQuery) {
    String username = user.username();
    log.info("### Fetching outcome requests for user: {}, searchQuery: {}", username, searchQuery);

    return userService.getAllUsers(username, FriendStatus.INVITATION_SENT, PageRequest.of(page, size), searchQuery);
  }
}