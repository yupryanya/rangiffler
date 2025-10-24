package guru.qa.rangiffler.repository;

import guru.qa.rangiffler.data.LogEntry;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface LogEntryRepository extends MongoRepository<LogEntry, String> {
}