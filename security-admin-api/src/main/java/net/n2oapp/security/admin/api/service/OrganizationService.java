package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import org.springframework.data.domain.Page;

/**
 * Сервис управления регионами
 */
public interface OrganizationService {

    /**
     * Найти все организации по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница найденных организаций
     */
    Page<Organization> findAll(OrganizationCriteria criteria);

    /**
     * Найти все категории организаций по критериям поиска
     *
     * @param criteria Критерии поиска
     * @return Страница категории организаций
     */
    Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria);

}
