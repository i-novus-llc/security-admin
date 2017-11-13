package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 *  Сервис управления ролями
 */


public interface RoleService {

    Integer create(Role role);

    Integer update(Role role);

    void delete(Integer id);

    Role  getById (Integer id);

    Page<Role> findAll (RoleCriteria criteria);
}
