package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.ApplicationCriteria;
import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import net.n2oapp.security.admin.api.model.Application;
import org.springframework.data.domain.Page;

/**
 * Сервис управления приложениями и системами
 */
public interface ApplicationSystemService {

    /**
     * Создать приложение
     *
     * @param service Модель приложения для создания
     * @return Созданное приложение
     */
    Application createApplication(Application service);

    /**
     * Изменить приложение
     *
     * @param service Модель приложения для изменения
     * @return Измененное приложение
     */
    Application updateApplication(Application service);

    /**
     * Удалить приложение
     *
     * @param code Код приложения
     */
    void deleteApplication(String code);

    /**
     * Получить приложение по коду
     *
     * @param code код приложения
     * @return Модель приложения
     */
    Application getApplication(String code);

    /**
     * Найти все приложения по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных приложений
     */
    Page<Application> findAllApplications(ApplicationCriteria criteria);

    /**
     * Существует ли приложение с таким кодом
     *
     * @param code код приложения
     * @return true - если существует, false иначе
     */
    Boolean isApplicationExist(String code);

    /**
     * Создать систему
     *
     * @param system Модель системы для создания
     * @return Созданная система
     */
    AppSystem createSystem(AppSystemForm system);

    /**
     * Изменить систему
     *
     * @param system Модель системы для изменения
     * @return Измененная система
     */
    AppSystem updateSystem(AppSystemForm system);

    /**
     * Удалить систему
     *
     * @param code Код системы
     */
    void deleteSystem(String code);

    /**
     * Получить систему по коду
     *
     * @param code код системы
     * @return Модель системы
     */
    AppSystem getSystem(String code);

    /**
     * Найти все системы по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных систем
     */
    Page<AppSystem> findAllSystems(SystemCriteria criteria);

    /**
     * Существует ли система с таким кодом
     *
     * @param code код системы
     * @return true - если существует, false иначе
     */
    Boolean isSystemExist(String code);

}
