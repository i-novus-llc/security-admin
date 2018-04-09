package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления ролями
 */
public interface RoleService<T extends RoleCriteria> {

    /**
     * Создать роль
     * @param role Модель роли для создания
     * @return Созданная роль
     */
    Role create(RoleForm role);

    /**
     * Изменить роль
     * @param role Модель роли для изменения
     * @return Измененная роль
     */
    Role update(RoleForm role);

    /**
     * Удалить роль
     * @param id Идентификатор роли
     */
    void delete(Integer id);

    /**
     * Получить роль по идентификатору
     * @param id Идентификатор роли
     * @return Модель роли
     */
    Role getById(Integer id);

    /**
     * Найти все роли по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных ролей
     */
    Page<Role> findAll(T criteria);
}
