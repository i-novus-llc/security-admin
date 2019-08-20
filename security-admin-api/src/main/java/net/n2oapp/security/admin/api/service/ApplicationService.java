package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.model.Application;
import net.n2oapp.security.admin.api.model.ApplicationForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления приложениями
 */
public interface ApplicationService {

    /**
     * Создать приложение
     * @param service Модель приложения для создания
     * @return Созданное приложение
     */
    Application create(ApplicationForm service);

    /**
     * Изменить приложение
     * @param service Модель приложения для изменения
     * @return Измененное приложение
     */
    Application update(ApplicationForm service);

    /**
     * Удалить приложение
     * @param code Код приложения
     */
    void delete(String code);

    /**
     * Получить приложение по коду
     * @param code код приложения
     * @return Модель приложения
     */
    Application getById(String code);

    /**
     * Найти все приложения по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных приложений
     */
    Page<Application> findAll(ApplicationCriteria criteria);

    /**
     * Существует ли приложение с таким кодом
     * @param code код приложения
     * @return     true - если существует, false иначе
     */
    Boolean isApplicationExist(String code);

}
