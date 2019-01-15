package net.n2oapp.security.admin.impl.entity;

import lombok.Data;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Clock;
import java.time.LocalDateTime;

@Data
@MappedSuperclass
public abstract class AbstractEntity implements Serializable {
    /**
     * Дата создания записи
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime creationDate;
    /**
     * Дата изменения версии
     */
    @Convert(converter = Jsr310JpaConverters.LocalDateTimeConverter.class)
    private LocalDateTime lastActionDate;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());

        if (creationDate == null) {
            creationDate = now;
        }
        if (lastActionDate == null) {
            lastActionDate = now;
        }
    }
}
