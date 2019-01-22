package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.EmployeeBankCriteria;
import net.n2oapp.security.admin.api.model.EmployeeBank;
import net.n2oapp.security.admin.api.model.EmployeeBankForm;
import org.springframework.data.domain.Page;

import java.util.UUID;

/**
 * Сервис управления уполномоченными лицами банка
 */
public interface EmployeeBankService {

    /**
     * Создать уполномоченное лицо
     * @param user Модель уполномоченного лица
     * @return Созданное уполномочееное лицо
     */
    EmployeeBank create(EmployeeBankForm user);


    /**
     * Поиск уполномоченного лица по банка
     * @param criteria Критерии поиска
     * @return Страница найденных уполномоченных лиц банка
     */
    Page<EmployeeBank> findByBank(EmployeeBankCriteria criteria);

    /**
     * Метод возвращает уполномоченнное лицо банка по ид-ру
     * @param id ид-р уполномоченного лица
     */
    EmployeeBank get(UUID id);
}
