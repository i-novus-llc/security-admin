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

    @Query("select count(r) from RoleEntity r where r.system.code = :systemCode")
    Integer countRolesWithSystemCode(@Param("systemCode") String systemCode);

    List<RoleEntity> findBySystem(SystemEntity systemEntity);

    @Query("SELECT CASE WHEN (COUNT(r) > 0) THEN false ELSE true END " +
            "FROM RoleEntity r WHERE r.id != :id AND (r.name = :name OR r.code = :code)")
    Boolean checkRoleUnique(@Param("id") Integer id, @Param("name") String name, @Param("code") String code);
}
