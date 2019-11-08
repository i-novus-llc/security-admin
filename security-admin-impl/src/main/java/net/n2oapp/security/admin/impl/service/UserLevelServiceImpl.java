package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;

import java.util.ArrayList;
import java.util.List;

/**
 * Сервис уровней пользователя
 */
public class UserLevelServiceImpl implements UserLevelService {

    private List<UserLevel> userLevels;
    private List<UserLevel> userLevelsForFilter;

    public UserLevelServiceImpl(List<UserLevel> userLevels) {
        this.userLevels = userLevels;
        userLevelsForFilter = new ArrayList<>(userLevels);
        userLevelsForFilter.add(UserLevel.NOT_SET);
    }

    @Override
    public List<UserLevel> getAll() {
        return userLevels;
    }

    @Override
    public List<UserLevel> getAllForFilter() {
        return userLevelsForFilter;
    }
}