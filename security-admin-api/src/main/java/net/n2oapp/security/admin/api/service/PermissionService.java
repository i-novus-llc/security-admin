package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.Permission;

import java.util.List;

/**
 * Сервис управления правами доступа
 */
public interface PermissionService {

    /**
     * Создать право доступа
     *
     * @param permission Модель права доступа
     * @return Созданное право доступа
     */
    Permission create(Permission permission);

    /**
     * Изменить право доступа
     *
     * @param permission Модель права доступа
     * @return Измененное право доступа
     */
    Permission update(Permission permission);

    /**
     * Удалить право доступа
     *
     * @param code Идентификатор права доступа
     */
    void delete(String code);

    /**
     * Получить право доступа по идентификатору
     *
     * @param code Идентификатор
     * @return Модель прав доступа
     */
    Permission getByCode(String code);

    /**
     * Найти все права доступаии поиска по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных всех прав доступа
     */
    List<Permission> getAll(PermissionCriteria criteria);

    /**
     * Найти все права доступаии поиска по идентификатору родительского элемента
     *
     * @param parentCode Идентификатор
     * @return Страница найденных дочерних прав доступа
     */
    List<Permission> getAllByParentCode(String parentCode);

    /**
     * Найти все права доступаии поиска по идентификатору родительского элемента
     *
     * @return Страница найденных корневых прав доступа
     */
    List<Permission> getAllByParentIdIsNull();
}
