package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.BankEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BankRepository extends JpaRepository<BankEntity, UUID>, JpaSpecificationExecutor<BankEntity> {

}

