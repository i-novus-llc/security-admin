package net.n2oapp.security.admin.Repo;


import net.n2oapp.security.admin.entity.ClientEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepo extends JpaRepository<ClientEntity, String> {
}
