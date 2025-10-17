package guru.qa.rangiffler.jupiter.extension;

import guru.qa.rangiffler.api.core.ThreadSafeCookieStore;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

public class CookiesExtension implements AfterTestExecutionCallback {
  @Override
  public void afterTestExecution(ExtensionContext context) throws Exception {
    ThreadSafeCookieStore.INSTANCE.removeAll();
  }
}