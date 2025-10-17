package guru.qa.rangiffler.data;

import guru.qa.rangiffler.model.PhotoStat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<PhotoEntity, UUID> {

  Page<PhotoEntity> findByUserIdIn(List<UUID> userIds, Pageable pageable);

  @Query("""
      SELECT p.countryCode AS country, COUNT(p) AS count
      FROM PhotoEntity p
      WHERE p.userId IN :userIds
      GROUP BY p.countryCode
      """)
  List<PhotoStat> findPhotoCountByUsersGroupedByCountry(@Param("userIds") List<UUID> userIds);

}