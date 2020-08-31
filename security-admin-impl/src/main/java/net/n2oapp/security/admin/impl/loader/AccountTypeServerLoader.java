package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.security.admin.api.model.AccountType;
import net.n2oapp.security.admin.api.model.AccountTypeRoleEnum;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.impl.entity.AccountTypeEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeRoleEntity;
import net.n2oapp.security.admin.impl.entity.AccountTypeRoleEntityId;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.repository.AccountTypeRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Загружает типы аккаунтов
 */
@Component
public class AccountTypeServerLoader implements ServerLoader<AccountType> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public AccountTypeServerLoader(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    @Transactional
    public void load(List<AccountType> data, String subject) {
        List<AccountTypeEntity> accountTypes = new ArrayList<>();
        List<AccountTypeEntity> oldAccountTypes = accountTypeRepository.findAll();
        for (AccountType model : data) {
            if (oldAccountTypes.stream().anyMatch(accountTypeEntity -> accountTypeEntity.getCode().equals(model.getCode())))
                continue;
            AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
            accountTypeEntity.setCode(model.getCode());
            accountTypeEntity.setName(model.getName());
            accountTypeEntity.setDescription(model.getDescription());
            accountTypeEntity.setUserLevel(model.getUserLevel());
            accountTypeEntity.setStatus(model.getStatus());
            List<String> userAndOrganizationRoleCode = new ArrayList<>();

            if (!CollectionUtils.isEmpty(model.getOrgRoles()) && !CollectionUtils.isEmpty(model.getRoles())) {
                model.getRoles().stream().forEach(role -> {
                    if (model.getOrgRoles().stream().anyMatch(orgRole -> orgRole.getCode().equals(role.getCode())))
                        userAndOrganizationRoleCode.add(role.getCode());
                });
                userAndOrganizationRoleCode.stream().forEach(roleCode -> {
                    model.setRoles(new ArrayList<>(model.getRoles()));
                    model.setOrgRoles(new ArrayList<>(model.getOrgRoles()));
                    Iterator<Role> userRoleIterator = model.getRoles().iterator();
                    Iterator<Role> orgRoleIterator = model.getOrgRoles().iterator();
                    while (userRoleIterator.hasNext()) {
                        if (userRoleIterator.next().getCode().equals(roleCode))
                            userRoleIterator.remove();
                    }
                    while (orgRoleIterator.hasNext()) {
                        if (orgRoleIterator.next().getCode().equals(roleCode))
                            orgRoleIterator.remove();
                    }
                });
            }
            if (nonNull(model.getRoles()))
                model.getRoles().stream().forEach(role -> addRole(accountTypeEntity, AccountTypeRoleEnum.USER_ROLE, role.getCode()));

            if (nonNull(model.getOrgRoles()))
                model.getOrgRoles().stream().forEach(role -> addRole(accountTypeEntity, AccountTypeRoleEnum.ORG_ROLE, role.getCode()));
            if (nonNull(userAndOrganizationRoleCode))
                userAndOrganizationRoleCode.stream().forEach(roleCode -> addRole(accountTypeEntity, AccountTypeRoleEnum.ORG_AND_USER_ROLE, roleCode));

            accountTypes.add(accountTypeEntity);
        }
        accountTypeRepository.saveAll(accountTypes);
    }

    @Override
    public String getTarget() {
        return "accountType";
    }

    @Override
    public Class<AccountType> getDataType() {
        return AccountType.class;
    }

    private void addRole(AccountTypeEntity accountType, AccountTypeRoleEnum roleType, String roleCode) {
        AccountTypeRoleEntity accountTypeRoleEntity = new AccountTypeRoleEntity();
        accountTypeRoleEntity.setRoleType(roleType);
        AccountTypeRoleEntityId accountTypeRoleEntityId = new AccountTypeRoleEntityId();
        accountTypeRoleEntityId.setAccountType(accountType);
        RoleEntity roleEntity = roleRepository.findOneByCode(roleCode);
        if (isNull(roleCode)) {
            Response response = Response.status(404).header("x-error-message", "role with code" + roleCode + "doesn't exists").build();
            throw new NotFoundException(response);
        }
        accountTypeRoleEntityId.setRole(roleEntity);
        accountTypeRoleEntity.setId(accountTypeRoleEntityId);
        if (isNull(accountType.getRoleList()))
            accountType.setRoleList(new ArrayList<>());
        accountType.getRoleList().add(accountTypeRoleEntity);
    }
}
