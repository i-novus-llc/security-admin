package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий систем
 */
@Repository
public interface SystemRepository extends JpaRepository<SystemEntity, String>, JpaSpecificationExecutor<SystemEntity> {
    SystemEntity findOneByCode(String code);
}
