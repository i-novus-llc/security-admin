package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.OrgCategoryCriteria;
import net.n2oapp.security.admin.api.criteria.OrganizationCriteria;
import net.n2oapp.security.admin.api.model.OrgCategory;
import net.n2oapp.security.admin.api.model.Organization;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.OrganizationService;
import net.n2oapp.security.admin.impl.entity.OrgCategoryEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
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
import java.util.stream.Collectors;

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
        if (organization.getCode() != null && organizationRepository.findByCode(organization.getCode()).isPresent())
            throw new UserException("exception.uniqueOrganizationCode");
        if (organizationRepository.findByOgrn(organization.getOgrn()).isPresent())
            throw new UserException("exception.uniqueOgrn");

        return model(organizationRepository.save(entity(organization)));
    }

    @Override
    public Organization update(Organization organization) {
        if (organization.getId() == null)
            throw new UserException("exception.NullOrganizationId");
        if (!organizationRepository.existsById(organization.getId()))
            throw new UserException("exception.OrganizationNotFound");
        if (organization.getCode() != null) {
            organizationRepository.findByCode(organization.getCode()).ifPresent(organizationEntity -> {
                if (!organizationEntity.getId().equals(organization.getId()))
                    throw new UserException("exception.uniqueOrganizationCode");
            });
        }
        return model(organizationRepository.save(entity(organization)));
    }

    @Override
    public void delete(Integer id) {
        OrganizationEntity organizationEntity = organizationRepository.findById(id).orElseThrow(() -> new UserException("exception.OrganizationNotFound"));
        if (!organizationEntity.getUsers().isEmpty())
            throw new UserException("exception.organizationHasUsers");
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
        model.setExtUid(entity.getExtUid());
        if (entity.getRoleList() != null) {
            model.setRoles(entity.getRoleList().stream()
                    .map(roleEntity -> {
                        Role role = new Role();
                        role.setId(roleEntity.getId());
                        role.setCode(roleEntity.getCode());
                        role.setName(roleEntity.getName());
                        return role;
                    })
                    .collect(Collectors.toList())
            );
        }
        if (entity.getCategories() != null) {
            model.setOrgCategories(entity.getCategories().stream()
                    .map(categoryEntity -> {
                        OrgCategory orgCategory = new OrgCategory();
                        orgCategory.setId(categoryEntity.getId());
                        orgCategory.setCode(categoryEntity.getCode());
                        orgCategory.setName(categoryEntity.getName());
                        orgCategory.setDescription(categoryEntity.getDescription());
                        return orgCategory;
                    })
                    .collect(Collectors.toList())
            );
        }
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
        organizationEntity.setExtUid(organization.getExtUid());
        if (organization.getRoleIds() != null)
            organizationEntity.setRoleList(organization.getRoleIds().stream().filter(roleId -> roleId > 0).map(RoleEntity::new).collect(Collectors.toList()));

        if (organization.getOrgCategoryIds() != null) {
            organizationEntity.setCategories(organization.getOrgCategoryIds().stream().map(categoryId -> {
                OrgCategoryEntity orgCategory = new OrgCategoryEntity();
                orgCategory.setId(categoryId);
                return orgCategory;
            }).collect(Collectors.toList()));
        }
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
