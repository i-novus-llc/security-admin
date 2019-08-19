package net.n2oapp.security.admin.impl.repository;


import net.n2oapp.security.admin.impl.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<ClientEntity, String> {
}
