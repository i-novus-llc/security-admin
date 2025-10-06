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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Service
@Transactional
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Autowired
    private AuditService auditService;
    @Autowired
    private Mapper mapper;

    public AccountServiceImpl(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Override
    public Page<Account> findAll(AccountCriteria criteria) {
        final Page<AccountEntity> all = accountRepository.findAll(new AccountSpecifications(criteria), criteria);
        return all.map(mapper::model);
    }

    @Override
    public Account getById(Integer id) {
        return mapper.model(accountRepository.findById(id)
                .orElseThrow(() -> new UserException("exception.accountNotFound")));
    }

    @Override
    public Account create(Account account) {
        AccountEntity accountEntity = entityForm(new AccountEntity(), account);
        accountEntity.setUser(new UserEntity(account.getUserId()));
        Account createdAccount = mapper.model(accountRepository.save(accountEntity));

        return audit("audit.accountCreate", createdAccount);
    }

    @Override
    public Account update(Account account) {
        AccountEntity accountEntity = accountRepository.findById(account.getId())
                .orElseThrow(() -> new UserException("exception.accountNotFound"));
        entityForm(accountEntity, account);
        Account updatedAccount = mapper.model(accountRepository.save(accountEntity));

        return audit("audit.accountUpdate", updatedAccount);
    }

    @Override
    public void delete(Integer id) {
        Account account = mapper.model(accountRepository.findById(id)
                .orElseThrow(() -> new UserException("exception.accountNotFound")));
        accountRepository.deleteById(id);

        if (nonNull(account))
            audit("audit.accountDelete", account);
    }

    @Override
    public Account changeActive(Integer id) {
        AccountEntity accountEntity = accountRepository.findById(id)
                .orElseThrow(() -> new UserException("exception.accountNotFound"));
        // TODO SECURITY-396 change current account activity exception

        accountEntity.setIsActive(!accountEntity.getIsActive());
        Account account = mapper.model(accountRepository.save(accountEntity));

        return audit("audit.accountChangeActive", account);
    }

    private AccountEntity entityForm(AccountEntity entity, Account model) {
        model.setRoles(excludeDummyRoles(model.getRoles()));
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

    private Account audit(String action, Account account) {
        auditService.audit(action, account, "" + account.getId(), "audit.account");
        return account;
    }

    /**
     * Значение roleId > 0, потому что в наборе могут присутствовать dummy роли, которые не должны быть учтены
     * и имеют отрицательное значение id.
     *
     * @param roles список ролей пользователя
     * @return отфильтрованный список ролей
     */
    private List<Role> excludeDummyRoles(List<Role> roles) {
        if (nonNull(roles))
            return roles.stream().filter(role -> role.getId() > 0).collect(Collectors.toList());
        return new ArrayList<>();
    }
}
