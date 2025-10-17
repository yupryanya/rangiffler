package guru.qa.rangiffler.page.people;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.defs.Actions.REMOVE;

public class FriendsTabPage extends PeoplePage<FriendsTabPage> {
  public FriendsTabPage() {
    super($("#simple-tabpanel-friends"));
  }

  @Step("Remove {username} from friends")
  public FriendsTabPage removeFromFriends(String username) {
    peopleTable.getActionsByUsername(username)
        .$(byText(REMOVE.getButtonText()))
        .click();
    return this;
  }
}
