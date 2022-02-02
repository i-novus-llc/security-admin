package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    private AuditService auditService;

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
        AccountEntity accountEntity = entityForm(new AccountEntity(), account);
        accountEntity.setUser(new UserEntity(account.getUserId()));
        Account createdAccount = model(accountRepository.save(accountEntity));

        return audit("audit.accountCreate", accountEntity);
    }

    @Override
    public Account update(Account account) {
        AccountEntity accountEntity = accountRepository.findById(account.getId())
                .orElseThrow(() -> new UserException("exception.accountNotFound"));
        entityForm(accountEntity, account);
        Account updatedAccount = model(accountRepository.save(accountEntity));

        return audit("audit.accountUpdate", updatedAccount);
    }

    @Override
    public void delete(Integer id) {
        Account account = model(accountRepository.findById(id)
                .orElseThrow(() -> new UserException("exception.accountNotFound")));
        accountRepository.deleteById(id);

        if (nonNull(account))
            audit("audit.accountDelete", account);
    }

    private AccountEntity entityForm(AccountEntity entity, Account model) {
        entity.setName(model.getName());
        entity.setUserLevel(nonNull(model.getUserLevel()) ? UserLevel.valueOf(model.getUserLevel()) : null);
        entity.setDepartment(nonNull(model.getDepartmentId()) ? new DepartmentEntity(model.getDepartmentId()) : null);
        entity.setOrganization(nonNull(model.getOrganizationId()) ? new OrganizationEntity(model.getOrganizationId()) : null);
        entity.setRegion(nonNull(model.getRegionId()) ? new RegionEntity(model.getRegionId()) : null);
        if (nonNull(model.getRoles()))
            entity.set.setRoleList(model.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        entity.setIsActive(model.getIsActive());
        return entity;
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

    private Account audit(String action, Account account) {
        auditService.audit(action, account, "" + account.getId(), "audit.account");
        return account;
    }
}
