package net.n2oapp.security.admin.impl.repository;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;


/**
 * Репозиторий пользователей
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer>, JpaSpecificationExecutor<UserEntity> {
    UserEntity findOneByUsername(String username);

    @Query("select count(u) from UserEntity u join u.roleList r where r.id = :roleId")
    Integer countUsersWithRoleId(@Param("roleId") Integer roleId);

}
