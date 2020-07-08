package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
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

import java.util.ArrayList;

/**
 * Реализация сервиса управления организациями
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
            criteria.setOrders(new ArrayList<>());
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<OrganizationEntity> all = organizationRepository.findAll(specification, criteria);
        return all.map(this::model);
    }

    @Override
    public Organization find(Integer id) {
        return model(organizationRepository.findById(id).orElseThrow(() -> new UserException("exception.OrganizationNotFound")));
    }

    @Override
    public Page<OrgCategory> findAllCategories(OrgCategoryCriteria criteria) {
        Specification<OrgCategoryEntity> specification = new OrgCategorySpecifications(criteria);
        if (criteria.getOrders() == null) {
            criteria.setOrders(new ArrayList<>());
            criteria.getOrders().add(new Sort.Order(Sort.Direction.ASC, "id"));
        }
        Page<OrgCategoryEntity> categories = orgCategoryRepository.findAll(specification, criteria);
        return categories.map(this::model);
    }

    @Override
    public Organization create(Organization organization) {
        if (organization.getId() != null && organizationRepository.existsById(organization.getId()))
            throw new UserException("exception.uniqueOrganization");
        if (organization.getCode() == null)
            throw new UserException("exception.NullOrganizationCode");
        else if (organizationRepository.findByCode(organization.getCode()).isPresent())
            throw new UserException("exception.uniqueOrganizationCode");

        return model(organizationRepository.save(entity(organization)));
    }

    @Override
    public Organization update(Organization organization) {
        if (!organizationRepository.existsById(organization.getId()))
            throw new UserException("exception.OrganizationNotFound");
        if (organization.getCode() == null)
            throw new UserException("exception.NullOrganizationCode");
        organizationRepository.findByCode(organization.getCode()).ifPresent(organizationEntity -> {
            if (!organizationEntity.getId().equals(organization.getId()))
                throw new UserException("exception.uniqueOrganizationCode");
        });
        return model(organizationRepository.save(entity(organization)));
    }

    @Override
    public void delete(Integer id) {
        if (!organizationRepository.existsById(id))
            throw new UserException("exception.OrganizationNotFound");
        organizationRepository.deleteById(id);
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
        model.setInn(entity.getInn());
        model.setLegalAddress(entity.getLegalAddress());
        model.setKpp(entity.getKpp());
        model.setEmail(entity.getEmail());
        return model;
    }

    private OrganizationEntity entity(Organization organization) {
        if (organization == null) return null;
        OrganizationEntity organizationEntity = new OrganizationEntity();
        organizationEntity.setId(organization.getId());
        organizationEntity.setCode(organization.getCode());
        organizationEntity.setShortName(organization.getShortName());
        organizationEntity.setOgrn(organization.getOgrn());
        organizationEntity.setOkpo(organization.getOkpo());
        organizationEntity.setFullName(organization.getFullName());
        organizationEntity.setInn(organization.getInn());
        organizationEntity.setKpp(organization.getKpp());
        organizationEntity.setLegalAddress(organization.getLegalAddress());
        organizationEntity.setEmail(organization.getEmail());
        return organizationEntity;
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
