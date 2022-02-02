package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class AccountServiceImpl implements AccountService {

    private AccountRepository accountRepository;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Account> findAll(AccountCriteria criteria) {
        final Page<AccountEntity> all = accountRepository.findAll(criteria);
        return all.map(this::model);
    }

    @Override
    public Account findById(Integer id) {
        return model(accountRepository.findById(id).orElse(null));
    }

    @Override
    public Account create(Account account) {
        return null;
    }

    @Override
    public Account update(Account account) {
        return null;
    }

    @Override
    public void delete(Integer id) {

    }

    private Account model(AccountEntity accountEntity) {
        if (isNull(accountEntity)) return null;
        Account account = new Account();
        account.setId(accountEntity.getId());
        account.setUserId(accountEntity.getUser().getId());
        account.setName(accountEntity.getName());
        account.setRoles(accountEntity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        account.setUserLevel(accountEntity.getUserLevel());
        account.setDepartment(model(accountEntity.getDepartment()));
        account.setRegion(model(accountEntity.getRegion()));
        account.setOrganization(model(accountEntity.getOrganization()));
        account.setExtSys(accountEntity.getExternalSystem());
        account.setExtUid(accountEntity.getExternalUid());
        return account;
    }

    private Role model(RoleEntity entity) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setNameWithSystem(entity.getName());
        if (nonNull(entity.getSystemCode()))
            model.setNameWithSystem(model.getNameWithSystem() + "(" + entity.getSystemCode().getName() + ")");

        return model;
    }

    private Department model(DepartmentEntity entity) {
        if (entity == null) return null;
        Department model = new Department();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        return model;
    }

    private Organization model(OrganizationEntity entity) {
        if (entity == null) return null;
        Organization model = new Organization();
        model.setId(entity.getId());
        model.setFullName(entity.getFullName());
        model.setShortName(entity.getShortName());
        model.setCode(entity.getCode());
        model.setOgrn(entity.getOgrn());
        model.setInn(entity.getInn());
        model.setOkpo(entity.getOkpo());
        return model;
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
}
