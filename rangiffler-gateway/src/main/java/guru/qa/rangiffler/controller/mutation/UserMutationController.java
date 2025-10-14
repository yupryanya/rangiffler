package guru.qa.rangiffler.controller.mutation;

import guru.qa.rangiffler.model.input.FriendshipInput;
import guru.qa.rangiffler.model.input.UserInput;
import guru.qa.rangiffler.model.type.UserGql;
import guru.qa.rangiffler.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@PreAuthorize("isAuthenticated()")
public class UserMutationController {
  private final UserService userService;

  @Autowired
  public UserMutationController(UserService userService) {
    this.userService = userService;
  }

  @MutationMapping
  public UserGql user(@AuthenticationPrincipal Jwt principal,
                      @Argument UserInput input) {
    String username = principal.getClaim("sub");
    log.info("### Updating user: {} with data {}", username, input);

    return userService.updateUserData(username, input);
  }

  @MutationMapping
  public UserGql friendship(@AuthenticationPrincipal Jwt principal,
                            @Argument FriendshipInput input) {
    String username = principal.getClaim("sub");
    log.info("### Adding a friendship {} for user: {}", input, username);

    return userService.updateFriendship(username, input);
  }
}