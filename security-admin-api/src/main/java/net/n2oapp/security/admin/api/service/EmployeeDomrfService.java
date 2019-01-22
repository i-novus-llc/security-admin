package net.n2oapp.security.admin.api.service;


import net.n2oapp.security.admin.api.criteria.EmployeeDomrfCriteria;
import net.n2oapp.security.admin.api.model.EmployeeDomrf;
import org.springframework.data.domain.Page;

/**
 * Сервис управления уполномоченными лицами ДОМ.РФ
 */
public interface EmployeeDomrfService {

    /**
     * Поиск уполномоченного лица по подразделению
     * @param criteria Критерии поиска
     * @return Страница найденных уполномоченных лиц ДОМ.РФ
     */
    Page<EmployeeDomrf> findByDepartment(EmployeeDomrfCriteria criteria);
}
