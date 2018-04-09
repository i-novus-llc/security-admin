package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.BaseCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import org.springframework.data.domain.Page;

/**
 *  Сервис управления правами доступа
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
     * @param permission Модель права доступа
     * @return Измененное право доступа
     */
    Permission update(Permission permission);

    /**
     * Удалить право доступа
     * @param id Идентификатор права доступа
     */
    void delete(Integer id);

    /**
     * Получить право доступа по идентификатору
     * @param id Идентификатор
     * @return Модель прав доступа
     */
    Permission getById(Integer id);

    /**
     * Найти все права доступа
     *
     * @return Страница найденных пользователей
     */
    Page<Permission> findAll(BaseCriteria criteria);

}
