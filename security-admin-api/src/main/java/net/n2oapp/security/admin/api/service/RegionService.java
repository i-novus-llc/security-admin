package net.n2oapp.security.admin.api.service;

import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.api.model.Region;
import org.springframework.data.domain.Page;

/**
 * Сервис управления регионами
 */
public interface RegionService {

    /**
     * Найти все регионы по критериям поиска
     * @param criteria Критерии поиска
     * @return Страница найденных регионов
     */
    Page<Region> findAll(RegionCriteria criteria);

}
