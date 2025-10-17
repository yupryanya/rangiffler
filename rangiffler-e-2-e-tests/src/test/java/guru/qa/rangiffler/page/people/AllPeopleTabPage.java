package guru.qa.rangiffler.page.people;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.defs.Actions.ADD;

public class AllPeopleTabPage extends PeoplePage<AllPeopleTabPage> {
  public AllPeopleTabPage() {
    super($("#simple-tabpanel-all"));
  }

  @Step("Send friend request to {username}")
  public AllPeopleTabPage sendFriendRequest(String username) {
    peopleTable.getActionsByUsername(username)
        .$(byText(ADD.getButtonText()))
        .click();
    return this;
  }
}
