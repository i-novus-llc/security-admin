package net.n2oapp.security.admin.impl.entity.base;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Objects;

@Getter
@Setter
@MappedSuperclass
public class RdmBaseEntity {

    private static final String DIRTY_STATE = "DIRTY";

    /**
     * Время удаления записи из справочника
     */
    @Column(name = "deleted_ts")
    protected LocalDateTime deletionDate;

    /**
     * Статус синхронизации записи
     */
    @Column(name = "rdm_sync_internal_local_row_state")
    protected String syncState;

    /**
     * @deprecated use deletionDate directly
     */
    @Deprecated(forRemoval = true)
    public Boolean getIsDeleted() {
        return deletionDate != null ? true : null;
    }

    /**
     * @deprecated use deletionDate directly
     */
    @Deprecated(forRemoval = true)
    public void setIsDeleted(Boolean isDeleted) {
        deletionDate = LocalDateTime.now(Clock.systemUTC());
    }

    @PrePersist
    protected void prePersist() {
        syncStateDefault();
    }

    @PreUpdate
    protected void preUpdate() {
        syncStateDefault();
    }

    private void syncStateDefault() {
        if (Objects.isNull(syncState))
            syncState = DIRTY_STATE;
    }
}
