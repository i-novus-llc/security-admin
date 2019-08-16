package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий служб
 */
@Repository
public interface ServiceRepository extends JpaRepository<ServiceEntity, String>, JpaSpecificationExecutor<ServiceEntity> {
}
