package guru.qa.rangiffler.test.web.people;

import guru.qa.rangiffler.jupiter.annotation.ApiLogin;
import guru.qa.rangiffler.jupiter.annotation.User;
import guru.qa.rangiffler.jupiter.annotation.meta.WebTest;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.people.FriendsTabPage;
import org.junit.jupiter.api.Test;

import java.util.List;

@WebTest
public class OutgoingRequestTest {
  private final FriendsTabPage peoplePage = new FriendsTabPage();

  @User(outgoingRequests = 3)
  @ApiLogin
  @Test
  void shouldDisplayOutgoingRequestsOnOutgoingRequestsTab(UserJson user) {
    List<UserJson> requests = user.testData().outgoingRequests();
    peoplePage.open()
        .toOutgoingRequestsTab()
        .shouldContainUsers(requests);
  }
}