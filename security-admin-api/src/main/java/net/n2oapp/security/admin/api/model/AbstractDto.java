package net.n2oapp.security.admin.api.model;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

@Data
public abstract class AbstractDto {

    private UUID id;

    private LocalDateTime creationDate;

    private LocalDateTime lastActionDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AbstractDto that = (AbstractDto) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "AbstractDto{" +
                "id=" + id +
                ", creationDate=" + creationDate +
                ", lastActionDate=" + lastActionDate +
                '}' + super.toString();
    }
}
