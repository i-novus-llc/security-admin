package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.UserLevelService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;

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
    public List<UserLevel> getAllForFilter(String name) {
        if (isNull(name))
            return userLevelsForFilter;
        List<UserLevel> result = new ArrayList<>();
        result.addAll(userLevelsForFilter.stream().filter(userLevel -> userLevel.getDesc().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList()));
        return result;
    }
}