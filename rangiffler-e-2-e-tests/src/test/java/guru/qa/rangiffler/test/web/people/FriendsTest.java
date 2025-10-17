package guru.qa.rangiffler.test.web.people;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.people.FriendsTabPage;
import org.junit.jupiter.api.Test;

import java.util.List;

import static guru.qa.rangiffler.defs.Actions.ADD;

@WebTest
public class FriendsTest {
  private final FriendsTabPage peoplePage = new FriendsTabPage();

  @User(friends = 3)
  @ApiLogin
  @Test
  void shouldDisplayFriendsOnFriendsTab(UserJson user) {
    List<UserJson> friends = user.testData().friends();
    peoplePage.open()
        .toFriendsTab()
        .shouldContainUsers(friends);
  }

  @User(friends = 1)
  @ApiLogin
  @Test
  void shouldRemoveFriendAndShowAddActionOnAllPeopleTab(UserJson user) {
    UserJson friend = user.testData().friends().getFirst();
    peoplePage.open()
        .toFriendsTab()
        .removeFromFriends(friend.username())
        .shouldNotContainUser(friend)
        .toAllPeopleTab()
        .shouldHaveActions(friend, ADD.getAction());
  }
}