package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.AccountTypeRoleEnum;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeRoleEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeRoleEntityId;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.AccountTypeRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.service.specification.AccountTypeSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository repository;
    private final RoleRepository roleRepository;

    @Autowired
    public AccountTypeServiceImpl(AccountTypeRepository repository, RoleRepository roleRepository) {
        this.repository = repository;
        this.roleRepository = roleRepository;
    }

    @Override
    public Page<AccountType> findAll(AccountTypeCriteria criteria) {
        Specification<AccountTypeEntity> specification = new AccountTypeSpecifications(criteria);
        return repository.findAll(specification, criteria).map(this::model);
    }

    @Override
    public AccountType findById(Integer id) {
        return model(repository.findById(id).orElseThrow());
    }

    @Override
    public AccountType create(AccountType accountType) {
        return model(repository.save(entity(accountType)));
    }

    @Override
    public AccountType update(AccountType accountType) {
        return model(repository.save(entity(accountType)));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    private AccountType model(AccountTypeEntity entity) {
        if (entity == null) return null;
        AccountType model = new AccountType();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        model.setDescription(entity.getDescription());
        model.setUserLevel(entity.getUserLevel());
        model.setStatus(entity.getStatus());
        mapModelRoles(entity, model);
        return model;
    }

    private void mapModelRoles(AccountTypeEntity entity, AccountType model) {
        if (entity.getRoleList() != null) {
            List<Role> roles = new ArrayList<>();
            for (AccountTypeRoleEntity accountTypeRoleEntity: entity.getRoleList()) {
                if (accountTypeRoleEntity.getRoleType().equals(AccountTypeRoleEnum.ORG_AND_USER_ROLE) ||
                        accountTypeRoleEntity.getRoleType().equals(AccountTypeRoleEnum.USER_ROLE)) {
                    Role role = new Role();
                    role.setId(accountTypeRoleEntity.getId().getRole().getId());
                    role.setCode(accountTypeRoleEntity.getId().getRole().getCode());
                    role.setName(accountTypeRoleEntity.getId().getRole().getName());
                    roles.add(role);
                }
            }
            model.setRoles(roles);
            roles = new ArrayList<>();
            for (AccountTypeRoleEntity accountTypeRoleEntity: entity.getRoleList()) {
                if (accountTypeRoleEntity.getRoleType().equals(AccountTypeRoleEnum.ORG_AND_USER_ROLE) ||
                        accountTypeRoleEntity.getRoleType().equals(AccountTypeRoleEnum.ORG_ROLE)) {
                    Role role = new Role();
                    role.setId(accountTypeRoleEntity.getId().getRole().getId());
                    role.setCode(accountTypeRoleEntity.getId().getRole().getCode());
                    role.setName(accountTypeRoleEntity.getId().getRole().getName());
                    roles.add(role);
                }
            }
            model.setOrgRoles(roles);
        }
    }

    private AccountTypeEntity entity(AccountType model) {
        if (model == null) return null;
        AccountTypeEntity entity = new AccountTypeEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setCode(model.getCode());
        entity.setDescription(model.getDescription());
        entity.setUserLevel(model.getUserLevel());
        entity.setStatus(model.getStatus());
        mapAndSetEntityRoles(model, entity);
        return entity;
    }

    private void mapAndSetEntityRoles(AccountType model, AccountTypeEntity entity) {
        List<AccountTypeRoleEntity> roleList = new ArrayList<>();
        mapOrgRoles(model, entity, roleList);
        mapUserRoles(model, entity, roleList);
        entity.setRoleList(roleList);
    }

    private void mapUserRoles(AccountType model, AccountTypeEntity entity, List<AccountTypeRoleEntity> roleList) {
        if (model.getOrgRoleIds() == null && model.getRoleIds() != null && !model.getRoleIds().isEmpty()) {
            roleList.addAll(mapRoleList(model.getRoleIds(), entity, AccountTypeRoleEnum.USER_ROLE));
        }
    }

    private void mapOrgRoles(AccountType model, AccountTypeEntity entity, List<AccountTypeRoleEntity> roleList) {
        if (model.getOrgRoleIds() != null && !model.getOrgRoleIds().isEmpty()) {
            if (model.getRoleIds() == null) {
                roleList.addAll(mapRoleList(model.getOrgRoleIds(), entity, AccountTypeRoleEnum.ORG_ROLE));
                return;
            }
            List<Integer> onlyOrgRoles = new ArrayList<>(model.getOrgRoleIds());
            onlyOrgRoles.removeAll(model.getRoleIds());
            if (!onlyOrgRoles.isEmpty()) {
                roleList.addAll(mapRoleList(onlyOrgRoles, entity, AccountTypeRoleEnum.ORG_ROLE));
            }
            roleList.addAll(mapRoleList(model.getRoleIds(), entity, AccountTypeRoleEnum.ORG_AND_USER_ROLE));
        }
    }

    private List<AccountTypeRoleEntity> mapRoleList(List<Integer> roleIds, AccountTypeEntity entity, AccountTypeRoleEnum roleType) {
        List<AccountTypeRoleEntity> roleList = new ArrayList<>();
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Integer roleId : roleIds) {
                AccountTypeRoleEntity accountTypeRole = new AccountTypeRoleEntity();
                accountTypeRole.setRoleType(roleType);
                AccountTypeRoleEntityId id = new AccountTypeRoleEntityId();
                id.setAccountType(entity);
                RoleEntity roleEntity = new RoleEntity();
                roleEntity.setId(roleId);
                id.setRole(roleEntity);
                accountTypeRole.setId(id);
                roleList.add(accountTypeRole);
            }
        }
        return roleList;
    }
}
