package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий приложений
 */
@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String>, JpaSpecificationExecutor<ApplicationEntity> {
    List<ApplicationEntity> findBySystemCode(SystemEntity system);
}
