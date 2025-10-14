package guru.qa.rangiffler.data.repository;

import guru.qa.rangiffler.data.FriendshipEntityStatus;
import guru.qa.rangiffler.data.entity.FriendshipEntity;
import guru.qa.rangiffler.data.entity.FriendshipId;
import guru.qa.rangiffler.data.entity.UserEntity;
import guru.qa.rangiffler.data.projection.UserWithStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface FriendshipRepository extends JpaRepository<FriendshipEntity, FriendshipId> {
  @Query("""
        select
            u.id as id,
            u.username as username,
            u.firstname as firstname,
            u.surname as surname,
            u.avatar as avatar,
            u.countryCode as countryCode,
            COALESCE(f.state, 'NONE') as friendshipState
        from UserEntity u
        left join FriendshipEntity f
            on f.friend.username = u.username
            and f.user.username = :requesterUsername
        where u.username <> :requesterUsername
          and (:state is null or f.state = :state)
          and (:searchQuery is null
                     or u.username like %:searchQuery%
                     or u.firstname like %:searchQuery%
                     or u.surname like %:searchQuery%)
    """)
  Page<UserWithStatus> findAllWithFriendshipState(@Param("requesterUsername") String requesterUsername,
                                                  @Param("state") FriendshipEntityStatus state,
                                                  @Param("searchQuery") String searchQuery,
                                                  Pageable pageable);

  Optional<FriendshipEntity> findByUserAndFriend(UserEntity user, UserEntity friend);
}
