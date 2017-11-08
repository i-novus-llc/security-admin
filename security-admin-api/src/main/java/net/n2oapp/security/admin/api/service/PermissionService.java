package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import org.springframework.stereotype.Service;

/**
 * Created by otihonova on 31.10.2017.
 */
@Service
public interface PermissionService {
    Integer create(Permission permission);

    Integer update(Permission permission);

    void delete(Integer id);

    Permission getById(Integer id);
}
