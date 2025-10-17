package guru.qa.rangiffler.data.logging;

import com.github.vertical_blank.sqlformatter.SqlFormatter;
import com.github.vertical_blank.sqlformatter.languages.Dialect;
import com.p6spy.engine.logging.Category;
import com.p6spy.engine.spy.appender.StdoutLogger;
import io.qameta.allure.attachment.AttachmentData;
import io.qameta.allure.attachment.AttachmentProcessor;
import io.qameta.allure.attachment.DefaultAttachmentProcessor;
import io.qameta.allure.attachment.FreemarkerAttachmentRenderer;
import org.apache.commons.lang3.StringUtils;

import static org.apache.commons.lang.StringUtils.substringBetween;

public class AllureAppender extends StdoutLogger {
  private final String templateName = "sql-attachment.ftl";
  private final AttachmentProcessor<AttachmentData> attachmentProcessor = new DefaultAttachmentProcessor();

  @Override
  public void logSQL(int connectionId, String now, long elapsed, Category category, String prepared, String sql, String url) {
    if(StringUtils.isNoneEmpty(sql)) {
      final SqlAttachmentData sqlAttachmentData = new SqlAttachmentData(
          sql.split("\\s+")[0] + " request to: " + substringBetween(url, "5432/", "?"),
          SqlFormatter.of(Dialect.PostgreSql).format(sql)
      );
      attachmentProcessor.addAttachment(
          sqlAttachmentData,
          new FreemarkerAttachmentRenderer(templateName)
      );
    }
  }
}
