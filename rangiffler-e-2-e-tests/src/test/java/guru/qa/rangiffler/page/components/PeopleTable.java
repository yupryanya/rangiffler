package guru.qa.rangiffler.page.components;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import guru.qa.rangiffler.model.UserJson;
import io.qameta.allure.Step;

import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.exactTexts;
import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$$;
import static guru.qa.rangiffler.condition.FriendsTableConditions.tableHasUsersAnyOrder;

public class PeopleTable extends BaseComponent<PeopleTable> {
  private final ElementsCollection tableRows = $$("table[aria-labelledby='tableTitle'] tbody tr");

  private static final Map<String, Integer> COLUMN_INDEX = Map.of(
      "Username", 0,
      "Name", 1,
      "Surname", 2,
      "Location", 3,
      "Actions", 4
  );

  public PeopleTable(SelenideElement self) {
    super(self);
  }

  private ElementsCollection getRowByUsername(String username) {
    return tableRows
        .findBy(text(username))
        .$$("td");
  }

  public SelenideElement getActionsByUsername(String username) {
    return getRowByUsername(username).get(COLUMN_INDEX.get("Actions"));
  }

  @Step("Verify that the table is displayed")
  public PeopleTable verifyTableDisplayed() {
    self.shouldBe(visible);
    return this;
  }

  @Step("Verify that table contains user")
  public PeopleTable shouldContainUser(UserJson user) {
    tableRows.should(tableHasUsersAnyOrder(user));
    return this;
  }

  @Step("Verify that table not contains user")
  public PeopleTable shouldNotContainUser(UserJson user) {
    tableRows.findBy(text(user.username())).shouldNot(exist);
    return this;
  }

  @Step("Verify actions present")
  public PeopleTable shouldHaveActions(UserJson user, String... actions) {
    getActionsByUsername(user.username())
        .shouldBe(visible)
        .$$("button")
        .shouldHave(exactTexts(actions));
    return this;
  }

  @Step("Click action {action} for user {user}" )
  public PeopleTable clickActionForUser(String action, UserJson user) {
    getActionsByUsername(user.username())
        .$(byText(action))
        .shouldBe(visible, enabled)
        .click();
    return this;
  }
}
