package guru.qa.rangiffler.data;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "photo")
public class PhotoEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID id;

  @Column
  private String description;

  @Lob
  @Column(columnDefinition = "LONGBLOB")
  private byte[] photo;

  @Column(name = "user_id", nullable = false, columnDefinition = "BINARY(16)")
  private UUID userId;

  @Column(name = "country_code", nullable = false, length = 5)
  private String countryCode;

  @Column
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  @ElementCollection
  @CollectionTable(
      name = "photo_like",
      joinColumns = @JoinColumn(name = "photo_id")
  )
  @Column(name = "user_id")
  private Set<UUID> likedUserIds = new HashSet<>();

  @Override
  public final boolean equals(Object o) {
    if (this == o) return true;
    if (o == null) return false;
    Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
    Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
    if (thisEffectiveClass != oEffectiveClass) return false;
    PhotoEntity that = (PhotoEntity) o;
    return getId() != null && Objects.equals(getId(), that.getId());
  }

  @Override
  public final int hashCode() {
    return this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
  }
}
