package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.RegionCriteria;
import net.n2oapp.security.admin.api.model.Region;
import net.n2oapp.security.admin.api.service.RegionService;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.repository.RegionRepository;
import net.n2oapp.security.admin.impl.service.specification.RegionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

/**
 * Реализация сервиса управления ролями
 */
@Service
@Transactional
public class RegionServiceImpl implements RegionService {
    @Autowired
    private RegionRepository regionRepository;
    
    @Override
    public Page<Region> findAll(RegionCriteria criteria) {
        Specification<RegionEntity> specification = new RegionSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<RegionEntity> all = regionRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    private Region model(RegionEntity entity) {
        if (entity == null) return null;
        Region model = new Region();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setOkato(entity.getOkato());
        return model;

    }

    public void setRegionRepository(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
}