package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.Criteria;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.data.domain.Page;

/**
 *  Сервис управления ролями
 */
public interface RoleService {

    Integer create(Role role);

    Integer update(Role role);

    void delete(Integer id);

    Role  getById (Integer id);

    Page<Role> findAll (Criteria criteria);
}
