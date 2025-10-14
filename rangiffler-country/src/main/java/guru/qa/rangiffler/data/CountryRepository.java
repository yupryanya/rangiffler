package guru.qa.rangiffler.data;

import jakarta.annotation.Nonnull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CountryRepository extends JpaRepository<CountryEntity, UUID> {
  Optional<CountryEntity> findByCode(@Nonnull String code);
}
