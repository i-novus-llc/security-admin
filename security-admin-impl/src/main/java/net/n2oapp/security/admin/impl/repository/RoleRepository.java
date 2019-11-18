package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Репозиторий ролей
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer>, JpaSpecificationExecutor<RoleEntity> {
    RoleEntity findOneByCode(String code);
    RoleEntity findOneByName(String name);

    @Query("select count(r) from RoleEntity r where r.systemCode.code = :systemCode")
    Integer countRolesWithSystemCode(@Param("systemCode") String systemCode);

    List<RoleEntity> findBySystemCode(SystemEntity systemEntity);

    void removeBySystemCode(SystemEntity systemEntity);
}
