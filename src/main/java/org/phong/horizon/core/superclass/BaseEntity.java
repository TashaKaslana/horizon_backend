package org.phong.horizon.core.superclass;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@MappedSuperclass
public abstract class BaseEntity extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false)
    private UUID id;

    @Override
    public final boolean equals(Object o) {
        if (this == o) return true;
        // Check if 'o' is null or if 'o' is a Hibernate proxy wrapping a different class
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o).getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this).getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;

        // Check if the ID is null (transient entity) or if the IDs match
        BaseEntity that = (BaseEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public final int hashCode() {
        // Use a fixed value for transient instances (before ID assignment),
        // and the ID's hashcode for persistent instances.
        // Using getClass().hashCode() ensures consistency with the equals check across proxies/classes.
        return Objects.hash(getClass());
    }

    // Consider adding a toString based on ID for logging if needed
    // @Override
    // public String toString() {
    //     return getClass().getSimpleName() + "[id=" + id + "]";
    // }
}