package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.repository.OrgCatRepository;
import net.n2oapp.security.admin.impl.repository.OrganizationRepository;
import net.n2oapp.security.admin.impl.service.specification.OrgCategorySpecifications;
import net.n2oapp.security.admin.impl.service.specification.OrganizationSpecifications;
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
    private OrganizationRepository organizationRepository;

    @Autowired
    private OrgCatRepository orgCategoryRepository;

    @Override
    public Page<Organization> findAll(OrganizationCriteria criteria) {
        Specification<OrganizationEntity> specification = new OrganizationSpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<OrganizationEntity> all = organizationRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria) {
        Specification<OrgCategoryEntity> specification = new OrgCategorySpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(Arrays.asList(new Sort.Order(Sort.Direction.ASC, "id")));
        } else {
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<OrgCategoryEntity> categories = orgCategoryRepository.findAll(specification, criteria);
        return categories.map(this::model);
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

    private OrgCategory model(OrgCategoryEntity entity) {
        if (entity == null) return null;
        OrgCategory model = new OrgCategory();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        return model;

    }
}
