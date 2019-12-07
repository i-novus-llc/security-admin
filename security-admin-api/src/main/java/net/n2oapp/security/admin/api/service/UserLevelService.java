package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.model.UserLevel;

import java.util.List;

/**
 * Сервис управления уровнем пользователя
 */
public interface UserLevelService {

    /**
     * Найти все уровни пользователя
     *
     * @return Страница всех уровней пользователя
     */
    List<UserLevel> getAll();

    /**
     * Найти все уровни пользователя для фильтра (включает значение 'Не задан')
     *
     * @return Страница всех уровней пользователя
     */
    List<UserLevel> getAllForFilter(String name);
}
