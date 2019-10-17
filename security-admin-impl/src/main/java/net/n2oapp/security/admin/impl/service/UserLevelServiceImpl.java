package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис уровней пользователя
 */
public class UserLevelServiceImpl implements UserLevelService {

    private List<UserLevel> userLevels;

    public UserLevelServiceImpl(List<UserLevel> userLevels) {
        this.userLevels = userLevels;
    }

    @Override
    public List<UserLevel> getAll() {
        return userLevels;
    }
}