package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Репозиторий пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {

    UserEntity findOneByUsernameIgnoreCase(String username);

    UserEntity findOneByEmailIgnoreCase(String email);

    Optional<UserEntity> findOneBySnilsIgnoreCase(String snils);

//    UserEntity findOneByExtUid(String extUid);

    void removeByUsernameIn(List<String> usernames);

    List<UserEntity> findByUsernameIn(List<String> usernames);
}
