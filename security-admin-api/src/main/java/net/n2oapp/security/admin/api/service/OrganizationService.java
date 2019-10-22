package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import org.springframework.data.domain.Page;

/**
 * Сервис управления регионами
 */
public interface OrganizationService {

    /**
     * Найти все регионы по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных регионов
     */
    Page<Organization> findAll(OrganizationCriteria criteria);

}
