package guru.qa.rangiffler.page.people;

import com.codeborne.selenide.Selenide;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.UserJson;
import guru.qa.rangiffler.page.BasePage;
import guru.qa.rangiffler.page.components.PeopleTable;
import guru.qa.rangiffler.page.components.SearchField;
import io.qameta.allure.Step;

import java.util.List;

import static com.codeborne.selenide.Selectors.byTagAndText;
import static com.codeborne.selenide.Selenide.$;

public abstract class PeoplePage<T extends PeoplePage<T>> extends BasePage<T> {
  protected final SelenideElement tabMenu = $("div[role='tablist']");

  protected final SearchField searchField;
  protected final PeopleTable peopleTable;

  public PeoplePage(SelenideElement root) {
    this.searchField = new SearchField(root);
    this.peopleTable = new PeopleTable(root);
  }

  protected String getUrl() {
    return CFG.frontUrl() + "people";
  }

  @Step("Open 'People' page")
  public T open() {
    return (T) Selenide.open(getUrl(), this.getClass());
  }

  @Step("Move to 'Friends' tab")
  public FriendsTabPage toFriendsTab() {
    tabMenu.$(byTagAndText("button", "Friends")).click();
    return new FriendsTabPage();
  }

  @Step("Move to 'All people' tab")
  public AllPeopleTabPage toAllPeopleTab() {
    tabMenu.$(byTagAndText("button", "All People")).click();
    return new AllPeopleTabPage();
  }

  @Step("Move to 'Income invitations' tab")
  public IncomeRequestsTabPage toIncomingRequestsTab() {
    tabMenu.$(byTagAndText("button", "Income invitations")).click();
    return new IncomeRequestsTabPage();
  }

  @Step("Move to 'Outcome invitations' tab")
  public OutgoingRequestsTabPage toOutgoingRequestsTab() {
    tabMenu.$(byTagAndText("button", "Outcome invitations")).click();
    return new OutgoingRequestsTabPage();
  }

  @Step("Verify user is present in table")
  public T shouldContainUser(UserJson user) {
    searchField.search(user.username());
    peopleTable.shouldContainUser(user);
    return (T) this;
  }

  @Step("Verify user is not present in table")
  public T shouldNotContainUser(UserJson user) {
    searchField.search(user.username());
    peopleTable.shouldNotContainUser(user);
    return (T) this;
  }

  @Step("Verify users are present in table")
  public T shouldContainUsers(List<UserJson> users) {
    for (UserJson user : users) {
      shouldContainUser(user);
    }
    return (T) this;
  }

  @Step("Verify user has actions")
  public T shouldHaveActions(UserJson user, String... actions) {
    searchField.search(user.username());
    peopleTable.shouldHaveActions(user, actions);
    return (T) this;
  }
}