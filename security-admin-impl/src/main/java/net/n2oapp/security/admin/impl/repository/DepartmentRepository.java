package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий департаментов
 */
@Repository
public interface DepartmentRepository extends JpaRepository<DepartmentEntity, Integer>, JpaSpecificationExecutor<DepartmentEntity> {

}
