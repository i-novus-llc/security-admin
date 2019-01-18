package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.DepartmentCriteria;
import net.n2oapp.security.admin.api.model.department.Department;
import net.n2oapp.security.admin.api.model.department.DepartmentCreateForm;
import net.n2oapp.security.admin.api.model.department.DepartmentUpdateForm;
import org.springframework.data.domain.Page;

/**
 * Сервис управления подразделениями ДОМ.РФ
 */
public interface DepartmentService {

    /**
     * Поиск подразделений по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных подразделений
     */
    Page<Department> findAll(DepartmentCriteria criteria);

    /**
     * Получить подразделение по идентификатору
     *
     * @param id Идентификатор
     * @return Модель подразделения
     */
    Department getById(String id);

    /**
     * Создать сведения о подразделении
     * @param department Модель подразделения
     * @return Созданное подразделение
     */
    Department create(DepartmentCreateForm department);

    /**
     * Изменение сведений о подразделении
     * @param department Модель подразделения
     * @return Измененное подразделение
     */
    Department update(DepartmentUpdateForm department);



}
