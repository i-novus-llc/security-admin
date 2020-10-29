package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.RegionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий ролей
 */
@Repository
public interface RegionRepository extends JpaRepository<RegionEntity, Integer>, JpaSpecificationExecutor<RegionEntity> {
    @Query("from RegionEntity r where r.code = ?1 and (r.isDeleted is null or r.isDeleted = false)")
    Optional<RegionEntity> findByCode(String code);
}
