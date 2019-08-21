package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.SystemCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.AppSystemForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления системами
 */
public interface AppSystemService {

    /**
     * Создать систему
     * @param system Модель системы для создания
     * @return Созданная система
     */
    AppSystem create(AppSystemForm system);

    /**
     * Изменить систему
     * @param system Модель системы для изменения
     * @return Измененная система
     */
    AppSystem update(AppSystemForm system);

    /**
     * Удалить систему
     * @param code Код системы
     */
    void delete(String code);

    /**
     * Получить систему по коду
     * @param code код системы
     * @return Модель системы
     */
    AppSystem getById(String code);

    /**
     * Найти все системы по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных систем
     */
    Page<AppSystem> findAll(SystemCriteria criteria);

    /**
     * Существует ли система с таким кодом
     * @param code код системы
     * @return     true - если существует, false иначе
     */
    Boolean isSystemExist(String code);

}
