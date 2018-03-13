package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import org.springframework.data.domain.Page;

/**
 * Сервис управления пользователями
 */
public interface UserService {

    /**
     * Создать пользователя
     * @param user Модель пользователя
     * @return Созданный пользователь
     */
    User create(User user);

    /**
     * Изменить пользователя
     * @param user Модель пользователя
     * @return Измененный пользователь
     */
    User update(User user);

    /**
     * Удалить пользователя
     * @param id Идентификатор пользователя
     */
    void delete(Integer id);

    /**
     * Получить пользователя по идентификатору
     * @param id Идентификатор
     * @return Модель пользователя
     */
    User getById (Integer id);

    /**
     * Найти всех пользователей по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных пользователей
     */
    Page<User> findAll (UserCriteria criteria);


}
