package guru.qa.rangiffler.data.entity;

import guru.qa.rangiffler.data.FriendshipEntityStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static guru.qa.rangiffler.data.FriendshipEntityStatus.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendship", uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "friend_id"}))
public class FriendshipEntity {

  @Id
  @Column(name = "id", columnDefinition = "BINARY(16)")
  private UUID id = UUID.randomUUID();

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private UserEntity user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "friend_id", nullable = false)
  private UserEntity friend;

  @Enumerated(EnumType.STRING)
  @Column(name = "state", nullable = false)
  private FriendshipEntityStatus state;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt = LocalDateTime.now();

  public FriendshipEntity(UserEntity user, UserEntity friend, FriendshipEntityStatus friendshipState) {
    this.user = user;
    this.friend = friend;
    this.state = friendshipState;
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  public void markUpdated() {
    this.updatedAt = LocalDateTime.now();
  }

  public static FriendshipEntity requester(UserEntity requester, UserEntity addressee) {
    return new FriendshipEntity(requester, addressee, FriendshipEntityStatus.SENT_PENDING);
  }

  public static FriendshipEntity addressee(UserEntity addressee, UserEntity requester) {
    return new FriendshipEntity(addressee, requester, FriendshipEntityStatus.RECEIVED_PENDING);
  }

  public void acceptRequest() {
    if (state != SENT_PENDING && state != RECEIVED_PENDING) {
      throw new IllegalStateException("Cannot accept request in friendshipState: " + state);
    }
    this.state = FRIEND;
    markUpdated();
  }

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    FriendshipEntity that = (FriendshipEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}