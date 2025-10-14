package guru.qa.rangiffler.controller.query;

import guru.qa.rangiffler.model.type.FeedGql;
import guru.qa.rangiffler.model.type.PhotoGql;
import guru.qa.rangiffler.service.FeedService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Slice;
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
public class FeedQueryController {
  private final FeedService feedService;

  @Autowired
  public FeedQueryController(FeedService feedService) {
    this.feedService = feedService;
  }

  @QueryMapping
  public FeedGql feed(@AuthenticationPrincipal Jwt principal,
                      @Argument boolean withFriends) {
    String username = principal.getClaim("sub");
    log.info("### Fetching feed for user: {}, withFriends: {}", username, withFriends);

    return feedService.getFeed(username, withFriends);
  }

  @SchemaMapping(typeName = "Feed", field = "photos")
  public Slice<PhotoGql> photos(FeedGql feed,
                                @Argument int page,
                                @Argument int size) {
    log.info("### Fetching feed photos for user: {}, withFriends: {}", feed.username(), feed.withFriends());

    return feedService.getPhotos(feed, page, size);
  }
}