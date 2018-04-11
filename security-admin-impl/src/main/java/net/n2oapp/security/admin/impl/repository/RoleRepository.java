package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий ролей
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer>, JpaSpecificationExecutor<RoleEntity> {
    RoleEntity findOneByCode(String code);
    RoleEntity findOneByName(String name);
}
