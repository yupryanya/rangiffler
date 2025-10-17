package guru.qa.rangiffler.page.people;

import static com.codeborne.selenide.Selenide.$;

public class OutgoingRequestsTabPage extends PeoplePage<OutgoingRequestsTabPage> {
  public OutgoingRequestsTabPage() {
    super($("#simple-tabpanel-outcome"));
  }
}
