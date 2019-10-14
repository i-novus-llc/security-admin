package net.n2oapp.security.admin.rest.impl;

import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.service.RegionService;
import net.n2oapp.security.admin.rest.api.RegionRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;


/**
 * Реализация REST сервиса управления регонами
 */
@Controller
public class RegionRestServiceImpl implements RegionRestService {
    @Autowired
    private RegionService service;

    @Override
    public Page<Region> getAll(RegionCriteria criteria) {
        return service.findAll(criteria);
    }
}
