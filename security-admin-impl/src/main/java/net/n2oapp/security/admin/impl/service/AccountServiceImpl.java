package net.n2oapp.security.admin.impl.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.audit.AuditService;
import net.n2oapp.security.admin.api.criteria.AccountCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.service.specification.AccountSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    private AuditService auditService;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Account> findAll(AccountCriteria criteria) {
        final Page<AccountEntity> all = accountRepository.findAll(new AccountSpecifications(criteria), criteria);
        return all.map(this::model);
    }

    @Override
    public Account getById(Integer id) {
        return model(accountRepository.findById(id)
                .orElseThrow(() -> new UserException("exception.accountNotFound")));
    }

    @Override
    public Account create(Account account) {
        AccountEntity accountEntity = entityForm(new AccountEntity(), account);
        accountEntity.setUser(new UserEntity(account.getUserId()));
        Account createdAccount = model(accountRepository.save(accountEntity));

        return audit("audit.accountCreate", createdAccount);
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
        entity.setUserLevel(nonNull(model.getUserLevel()) ? model.getUserLevel() : null);
        entity.setDepartment(nonNull(model.getDepartment()) ? new DepartmentEntity(model.getDepartment().getId()) : null);
        entity.setOrganization(nonNull(model.getOrganization()) ? new OrganizationEntity(model.getOrganization().getId()) : null);
        entity.setRegion(nonNull(model.getRegion()) ? new RegionEntity(model.getRegion().getId()) : null);
        if (nonNull(model.getRoles()))
            entity.setRoleList(model.getRoles().stream().map(r -> new RoleEntity(r.getId())).collect(Collectors.toList()));
        entity.setIsActive(model.getIsActive());
        return entity;
    }

    private Account model(AccountEntity entity) {
        if (isNull(entity)) return null;
        Account account = new Account();
        account.setId(entity.getId());
        account.setUserId(entity.getUser().getId());
        account.setName(entity.getName());
        account.setIsActive(entity.getIsActive());
        account.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
        account.setUserLevel(entity.getUserLevel());
        account.setDepartment(model(entity.getDepartment()));
        account.setRegion(model(entity.getRegion()));
        account.setOrganization(model(entity.getOrganization()));
        account.setExtSys(entity.getExternalSystem());
        account.setExtUid(entity.getExternalUid());
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
