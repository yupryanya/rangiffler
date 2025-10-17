package guru.qa.rangiffler.page.people;

import io.qameta.allure.Step;

import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static guru.qa.rangiffler.defs.Actions.ACCEPT;
import static guru.qa.rangiffler.defs.Actions.DECLINE;

public class IncomeRequestsTabPage extends PeoplePage<IncomeRequestsTabPage> {
  public IncomeRequestsTabPage() {
    super($("#simple-tabpanel-income"));
  }

  @Step("Accept incoming request from {username}")
  public IncomeRequestsTabPage acceptIncomingRequestFrom(String username) {
    peopleTable.getActionsByUsername(username)
        .$(byText(ACCEPT.getButtonText()))
        .click();
    return this;
  }

  @Step("Decline incoming request from {username}")
  public IncomeRequestsTabPage declineIncomingRequestFrom(String username) {
    peopleTable.getActionsByUsername(username)
        .$(byText(DECLINE.getButtonText()))
        .click();
    return this;
  }
}