package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.config.Config;
import guru.qa.rangiffler.data.jpa.EntityManagers;

public class DatabasesExtension implements SuiteExtension {
  protected static final Config CFG = Config.getInstance();

  @Override
  public void afterSuite() {
    EntityManagers.closeAllConnections();
  }
}