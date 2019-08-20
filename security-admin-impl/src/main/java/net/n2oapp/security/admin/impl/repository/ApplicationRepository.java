package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.ApplicationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий приложений
 */
@Repository
public interface ApplicationRepository extends JpaRepository<ApplicationEntity, String>, JpaSpecificationExecutor<ApplicationEntity> {
}
