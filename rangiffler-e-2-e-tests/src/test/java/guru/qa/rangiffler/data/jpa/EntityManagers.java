package guru.qa.rangiffler.data.jpa;

import guru.qa.rangiffler.data.tpl.DataSources;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityManagers {
  private static final Map<String, EntityManagerFactory> emfs = new ConcurrentHashMap<>();

  public static @Nullable EntityManager em(@Nonnull String jdbcUrl) {
    return new ThreadSafeEntityManager(
        emfs.computeIfAbsent(
            jdbcUrl,
            key -> {
              DataSources.dataSource(jdbcUrl);
              final String persistenceUnitName = StringUtils.substringAfterLast(jdbcUrl, "/");
              return Persistence.createEntityManagerFactory(persistenceUnitName);
            }
        ).createEntityManager()
    );
  }

  public static void closeAllConnections() {
    emfs.values().forEach(EntityManagerFactory::close);
  }
}
