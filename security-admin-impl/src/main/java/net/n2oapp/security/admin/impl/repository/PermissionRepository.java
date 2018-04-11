package net.n2oapp.security.admin.impl.repository;


import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Репозиторий прав доступа
 */
@Repository
public interface PermissionRepository  extends JpaRepository<PermissionEntity, Integer>,JpaSpecificationExecutor<PermissionEntity> {
    List<PermissionEntity> findByParentId(Integer parentId);

    List<PermissionEntity> findByParentIdIsNull();
}
