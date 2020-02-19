package net.n2oapp.security.admin.impl.repository;


import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Репозиторий категорий организаций
 */
@Repository
public interface OrgCatRepository extends JpaRepository<OrgCategoryEntity, Integer>, JpaSpecificationExecutor<OrgCategoryEntity> {
}
