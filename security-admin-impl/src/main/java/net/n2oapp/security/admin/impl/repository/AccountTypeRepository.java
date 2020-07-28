package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий типов аккаунтов
 */
@Repository
public interface AccountTypeRepository extends JpaRepository<AccountTypeEntity, Integer>, JpaSpecificationExecutor<AccountTypeEntity> {
    Optional<AccountTypeEntity> findByCode(String code);
}
