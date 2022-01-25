package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий аккаунтов
 */
@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Integer>, JpaSpecificationExecutor<AccountEntity> {
}
