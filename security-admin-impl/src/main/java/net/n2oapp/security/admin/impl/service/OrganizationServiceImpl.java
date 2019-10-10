package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.repository.OrganizationRepository;
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
public class OrganizationServiceImpl implements OrganizationService {
    @Autowired
    private OrganizationRepository regionRepository;
    
    @Override
    public Page<Organization> findAll(OrganizationCriteria criteria){
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<OrganizationEntity> all = regionRepository.findAll(criteria);
        return all.map(this::model);
    }

    private Organization model(OrganizationEntity entity) {
        if (entity == null) return null;
        Organization model = new Organization();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setCode(entity.getCode());
        model.setOgrn(entity.getOgrn());
        model.setOkpo(entity.getOkpo());
        return model;

    }

    public void setOrganizationRepository(OrganizationRepository regionRepository) {
        this.regionRepository = regionRepository;
    }
}
