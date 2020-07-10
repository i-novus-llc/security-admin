package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий организаций
 */
@Repository
public interface OrganizationRepository extends JpaRepository<OrganizationEntity, Integer>, JpaSpecificationExecutor<OrganizationEntity> {
    Optional<OrganizationEntity> findByCode(String code);
}
