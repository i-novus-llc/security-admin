package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Created by otihonova on 31.10.2017.
 */
@Repository
public interface PermissionRepository  extends JpaRepository<PermissionEntity, Integer>,JpaSpecificationExecutor<PermissionEntity> {
}
