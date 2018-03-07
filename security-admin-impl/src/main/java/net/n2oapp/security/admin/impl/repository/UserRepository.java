package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {

}
