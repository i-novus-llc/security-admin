package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.EmployeeDomrfEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface EmployeeDomrfRepository extends JpaRepository<EmployeeDomrfEntity, UUID>, JpaSpecificationExecutor<EmployeeDomrfEntity> {
    @Query("select u from EmployeeDomrfEntity u  join  u.department d where  d.id = :departmentId")
    List<EmployeeDomrfEntity> findByDepartmentId(@Param("departmentId") UUID departmentId);
}
