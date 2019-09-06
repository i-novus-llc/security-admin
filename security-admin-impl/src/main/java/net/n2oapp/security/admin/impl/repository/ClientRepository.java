package net.n2oapp.security.admin.impl.repository;


import net.n2oapp.security.admin.impl.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Репозиторий клиентов
 */
@Repository
public interface ClientRepository extends JpaRepository<ClientEntity, String>, JpaSpecificationExecutor<ClientEntity> {
    Optional<ClientEntity> findByClientId(String clientId);
}
