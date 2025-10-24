package guru.qa.rangiffler.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@NoArgsConstructor
@Data
@Document(collection = "user_logs")
public class LogEntry {
  @Id
  private String id;

  private Date timestamp;
  private String app;
  private String level;
  private String message;
  private String activeProfile;
}
