package guru.qa.rangiffler.service;

import guru.qa.rangiffler.model.FriendStatus;
import guru.qa.rangiffler.model.type.FeedGql;
import guru.qa.rangiffler.model.type.PhotoGql;
import guru.qa.rangiffler.model.type.StatGql;
import guru.qa.rangiffler.model.type.UserGql;
import guru.qa.rangiffler.service.photo.PhotoService;
import guru.qa.rangiffler.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Stream;

@Slf4j
@Service
public class FeedService {

  private final UserService userService;
  private final PhotoService photoService;

  @Autowired
  public FeedService(UserService userService,
                     PhotoService photoService) {
    this.userService = userService;
    this.photoService = photoService;
  }

  public FeedGql getFeed(String username, boolean withFriends) {
    log.info("### Fetching feed for user '{}' withFriends={}", username, withFriends);

    UUID userId = userService.getUserId(username);
    List<UUID> userIds = withFriends
        ? Stream.concat(
        userService.getAllUsers(username, FriendStatus.FRIEND, PageRequest.of(0, 100), "")
            .stream()
            .map(UserGql::id),
        Stream.of(userId)
    ).toList()
        : List.of(userId);

    List<StatGql> stats = photoService.getUserStats(userIds);

    return new FeedGql(username, withFriends, null, stats);
  }

  public Slice<PhotoGql> getPhotos(FeedGql feed, int page, int size) {
    String username = feed.username();
    UUID userId = userService.getUserId(username);
    boolean withFriends = feed.withFriends();
    log.info("### Fetching feed photos for user '{}' withFriends={}, page={}, size={}", username, withFriends, page, size);
    List<UUID> userIds = withFriends
        ? Stream.concat(
            userService.getAllUsers(username, FriendStatus.FRIEND, PageRequest.of(0, 100), "")
                .stream()
                .map(UserGql::id),
            Stream.of(userId)
        )
        .toList()
        : List.of(userId);
    return photoService.getUsersPhotos(userIds, page, size);
  }
}