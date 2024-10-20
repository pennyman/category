package org.musinsa.category.domain;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Version;
import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractDomain<E extends AbstractDomain, PK extends Serializable> implements Persistable<PK>,
        Serializable {

    @Version
    @Column(name = "VER_NO", nullable = false)
    private Long version;

    @Column(name = "CREATE_DT", nullable = false)
    @CreatedDate
    private LocalDateTime createdDate;

    @Column(name = "UPDATE_DT")
    @LastModifiedDate
    private LocalDateTime lastModifiedDate;

    @Column(name = "DEL_DT")
    private LocalDateTime deletedDate;

    @Override
    public boolean isNew() {
        return null == getId();
    }

    public boolean isDeleted() {
        return deletedDate != null;
    }

    public void delete() {
        deletedDate = LocalDateTime.now();
    }

    public void incrementVersion() {
        this.version = this.version + 1;
    }
}
