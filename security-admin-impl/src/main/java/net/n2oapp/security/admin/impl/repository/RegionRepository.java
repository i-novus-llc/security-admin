package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий ролей
 */
@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer>, JpaSpecificationExecutor<RegionEntity> {

}
