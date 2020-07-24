package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.AccountTypeCriteria;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.AccountTypeService;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeRoleEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.AccountTypeRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.service.specification.AccountTypeSpecifications;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@Transactional
public class AccountTypeServiceImpl implements AccountTypeService {

    private final AccountTypeRepository repository;
    private final RoleRepository roleRepository;

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
        if (entity.getRoleList() != null) {
            model.setRoles(entity.getRoleList().stream().map(e -> {
                Role role = new Role();
                role.setId(e.getRole().getId());
                role.setCode(e.getRole().getCode());
                role.setName(e.getRole().getName());
                return role;
            }).collect(Collectors.toList()));
        }
        return model;
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
        if (model.getRoleIds() != null) {
            entity.setRoleList(
                    model.getRoleIds().stream().map(m -> {
                        AccountTypeRoleEntity accountTypeRole = new AccountTypeRoleEntity();
                        accountTypeRole.setAccountType(entity);
                        RoleEntity roleEntity = new RoleEntity();
                        roleEntity.setId(m);
                        accountTypeRole.setRole(roleEntity);
                        return accountTypeRole;
                    }).collect(Collectors.toList())
            );
        }
        return entity;
    }
}
