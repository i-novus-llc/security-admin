package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.EmployeeBankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface EmployeeBankRepository extends JpaRepository<EmployeeBankEntity, UUID>, JpaSpecificationExecutor<EmployeeBankEntity> {
    @Query("select u from EmployeeBankEntity u  join  u.bank r where  r.id = :bankId")
    List<EmployeeBankEntity> findByBankId(@Param("bankId") UUID bankId);
}
