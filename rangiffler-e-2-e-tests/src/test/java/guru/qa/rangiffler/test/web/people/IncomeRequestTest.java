package guru.qa.rangiffler.test.web.people;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.people.FriendsTabPage;
import org.junit.jupiter.api.Test;

import java.util.List;

@WebTest
public class IncomeRequestTest {
  private final FriendsTabPage peoplePage = new FriendsTabPage();

  @User(incomingRequests = 3)
  @ApiLogin
  @Test
  void shouldDisplayIncomeRequestsOnIncomingRequestsTab(UserJson user) {
    List<UserJson> requests = user.testData().incomingRequests();
    peoplePage.open()
        .toIncomingRequestsTab()
        .shouldContainUsers(requests);
  }

  @User(incomingRequests = 1)
  @ApiLogin
  @Test
  void shouldAcceptIncomingFriendRequest(UserJson user) {
    UserJson incomeUser = user.testData().incomingRequests().getFirst();
    peoplePage.open()
        .toIncomingRequestsTab()
        .acceptIncomingRequestFrom(incomeUser.username())
        .shouldNotContainUser(incomeUser)
        .toFriendsTab()
        .shouldContainUser(incomeUser);
  }

  @User(incomingRequests = 1)
  @ApiLogin
  @Test
  void shouldDeclineIncomingFriendRequest(UserJson user) {
    UserJson incomeUser = user.testData().incomingRequests().getFirst();
    peoplePage.open()
        .toIncomingRequestsTab()
        .declineIncomingRequestFrom(incomeUser.username())
        .shouldNotContainUser(incomeUser)
        .toAllPeopleTab()
        .shouldContainUser(incomeUser);
  }

  @User
  @ApiLogin
  @Test
  void shouldNotAllowUserToAddThemselves(UserJson user) {
    peoplePage.open()
        .toAllPeopleTab()
        .shouldNotContainUser(user);
  }
}
