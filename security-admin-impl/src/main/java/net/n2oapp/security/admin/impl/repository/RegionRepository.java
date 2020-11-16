package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий ролей
 */
@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer>, JpaSpecificationExecutor<RegionEntity> {
}
