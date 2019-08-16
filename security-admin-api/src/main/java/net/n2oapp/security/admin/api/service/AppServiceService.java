package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.ServiceCriteria;
import net.n2oapp.security.admin.api.model.AppService;
import net.n2oapp.security.admin.api.model.AppServiceForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления службами
 */
public interface AppServiceService {

    /**
     * Создать службу
     * @param service Модель службы для создания
     * @return Созданная служба
     */
    AppService create(AppServiceForm service);

    /**
     * Изменить службу
     * @param service Модель службы для изменения
     * @return Измененная служба
     */
    AppService update(AppServiceForm service);

    /**
     * Удалить службу
     * @param code Код службы
     */
    void delete(String code);

    /**
     * Получить службу по коду
     * @param code код системы
     * @return Модель системы
     */
    AppService getById(String code);

    /**
     * Найти все службы по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных служб
     */
    Page<AppService> findAll(ServiceCriteria criteria);

    /**
     * Существует ли система с таким кодом
     * @param code код системы
     * @return     true - если существует, false иначе
     */
    Boolean isServiceExist(String code);

}
