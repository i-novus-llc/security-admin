package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.SystemEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Репозиторий прав доступа
 */
@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, String>,
        JpaSpecificationExecutor<PermissionEntity> {
    List<PermissionEntity> findByParentCode(String parentCode);

    List<PermissionEntity> findByParentCodeIsNull();

    List<PermissionEntity> findBySystemCodeOrderByCodeDesc(SystemEntity system);

    @Query("select count(r) from PermissionEntity r where r.systemCode.code = :systemCode")
    Integer countPermissionsWithSystemCode(@Param("systemCode") String systemCode);
}
