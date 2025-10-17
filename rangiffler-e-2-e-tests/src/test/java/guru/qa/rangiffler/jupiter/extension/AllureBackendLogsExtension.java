package guru.qa.rangiffler.jupiter.extension;

import io.qameta.allure.Allure;
import io.qameta.allure.AllureLifecycle;
import io.qameta.allure.model.TestResult;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.UUID;

@Slf4j
public class AllureBackendLogsExtension implements SuiteExtension {
  public static final String caseName = "Rangiffler backend logs";
  String[] services = {
      "rangiffler-country",
      "rangiffler-gateway",
      "rangiffler-photo",
      "rangiffler-userdata"
  };

  @SneakyThrows
  @Override
  public void afterSuite() {
    if ("docker".equals(System.getProperty("test.env"))) {
      log.info("Skipping backend logs attachment in Docker environment");
    } else {
      AllureLifecycle allureLifecycle = Allure.getLifecycle();
      final String caseId = UUID.randomUUID().toString();
      allureLifecycle.scheduleTestCase(new TestResult().setUuid(caseId).setName(caseName));
      allureLifecycle.startTestCase(caseId);

      for (String service : services) {
        Path logPath = Path.of("./logs/" + service + "/spring.log");
        if (Files.exists(logPath)) {
          allureLifecycle.addAttachment(
              service + " log",
              "text/html",
              ".log",
              Files.newInputStream(logPath)
          );
        } else {
          log.info("Log file not found: " + logPath);
        }
      }

      allureLifecycle.stopTestCase(caseId);
      allureLifecycle.writeTestCase(caseId);
    }
  }
}