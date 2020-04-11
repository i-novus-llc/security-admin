package net.n2oapp.security.admin.rest.client;

import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.service.RegionService;
import net.n2oapp.security.admin.rest.api.RegionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRegionCriteria;
import org.springframework.data.domain.Page;

/**
 * Прокси реализация сервиса управления регионами, для вызова rest
 */
public class RegionServiceRestClient implements RegionService {

    private RegionRestService client;

    public RegionServiceRestClient(RegionRestService client) {
        this.client = client;
    }

    @Override
    public Page<Region> findAll(RegionCriteria criteria) {
        RestRegionCriteria regionCriteria = new RestRegionCriteria();
        regionCriteria.setPage(criteria.getPageNumber());
        regionCriteria.setSize(criteria.getPageSize());
        regionCriteria.setName(criteria.getName());
        regionCriteria.setOrders(criteria.getOrders());
        return client.getAll(regionCriteria);
    }

}
