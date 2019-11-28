package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;
import net.n2oapp.security.admin.rest.api.UserLevelRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления уровнями пользователя
 */
@Controller
public class UserLevelRestServiceImpl implements UserLevelRestService {
    @Autowired
    private UserLevelService service;

    @Override
    public Page<UserLevel> getAll() {
        return new PageImpl<>( service.getAll());
    }

    @Override
    public Page<UserLevel> getAllForFilter(String name) {
        return new PageImpl<>(service.getAllForFilter(name));
    }
}
