package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.Department;
import org.springframework.data.domain.Page;

/**
 * Сервис управления депатраментами
 */
public interface DepartmentService {

    /**
     * Найти все департаменты по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных департаментов
     */
    Page<Department> findAll(DepartmentCriteria criteria);

}
