package net.n2oapp.security.admin.impl.loader;

import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.ServerLoaderSettings;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * Загружает типы аккаунтов
 */
@Component
public class AccountTypeServerLoader extends ServerLoaderSettings<AccountType> implements ServerLoader<AccountType> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AccountTypeRepository accountTypeRepository;

    public AccountTypeServerLoader(AccountTypeRepository accountTypeRepository) {
        this.accountTypeRepository = accountTypeRepository;
    }

    @Override
    public String getTarget() {
        return "accountType";
    }

    @Override
    public Class<AccountType> getDataType() {
        return AccountType.class;
    }

    @Override
    @Transactional
    public void load(List<AccountType> data, String subject) {
        List<AccountTypeEntity> accountTypes = new ArrayList<>();
        List<AccountTypeEntity> oldAccountTypes = accountTypeRepository.findAll();
        for (AccountType model : data) {
            if (oldAccountTypes.stream().noneMatch(accountTypeEntity -> accountTypeEntity.getCode().equals(model.getCode())))
                accountTypes.add(toEntity(model));
        }
        if (isCreateRequired())
            accountTypeRepository.saveAll(accountTypes);
    }

    private AccountTypeEntity toEntity(AccountType model) {
        AccountTypeEntity accountTypeEntity = new AccountTypeEntity();
        accountTypeEntity.setCode(model.getCode());
        accountTypeEntity.setName(model.getName());
        accountTypeEntity.setDescription(model.getDescription());
        accountTypeEntity.setUserLevel(model.getUserLevel());
        accountTypeEntity.setStatus(model.getStatus());

        List<String> userAndOrganizationRoleCodes = extractUserAndOrganizationRoleCodes(model);
        addRoles(accountTypeEntity, AccountTypeRoleEnum.USER_ROLE, model.getRoles());
        addRoles(accountTypeEntity, AccountTypeRoleEnum.ORG_ROLE, model.getOrgRoles());
        userAndOrganizationRoleCodes.forEach(roleCode -> addRole(accountTypeEntity, AccountTypeRoleEnum.ORG_AND_USER_ROLE, roleCode));
        return accountTypeEntity;
    }

    private List<String> extractUserAndOrganizationRoleCodes(AccountType model) {
        if (CollectionUtils.isEmpty(model.getOrgRoles()) || CollectionUtils.isEmpty(model.getRoles()))
            return new ArrayList<>();
        List<String> roleCodes = model.getRoles().stream()
                .map(Role::getCode)
                .filter(code -> model.getOrgRoles().stream().anyMatch(orgRole -> orgRole.getCode().equals(code)))
                .toList();
        if (!roleCodes.isEmpty()) {
            model.setRoles(excludeRoles(model.getRoles(), roleCodes));
            model.setOrgRoles(excludeRoles(model.getOrgRoles(), roleCodes));
        }
        return roleCodes;
    }

    private List<Role> excludeRoles(List<Role> roles, List<String> roleCodes) {
        return roles.stream()
                .filter(role -> !roleCodes.contains(role.getCode()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    private void addRoles(AccountTypeEntity accountType, AccountTypeRoleEnum roleType, List<Role> roles) {
        if (nonNull(roles))
            roles.forEach(role -> addRole(accountType, roleType, role.getCode()));
    }

    private void addRole(AccountTypeEntity accountType, AccountTypeRoleEnum roleType, String roleCode) {
        AccountTypeRoleEntity accountTypeRoleEntity = new AccountTypeRoleEntity();
        accountTypeRoleEntity.setRoleType(roleType);
        AccountTypeRoleEntityId accountTypeRoleEntityId = new AccountTypeRoleEntityId();
        accountTypeRoleEntityId.setAccountType(accountType);
        RoleEntity roleEntity = roleRepository.findOneByCode(roleCode);
        if (isNull(roleEntity)) {
            Response response = Response.status(404).header("x-error-message", "role with code " + roleCode + " doesn't exist").build();
            throw new NotFoundException(response);
        }
        accountTypeRoleEntityId.setRole(roleEntity);
        accountTypeRoleEntity.setId(accountTypeRoleEntityId);
        if (isNull(accountType.getRoleList()))
            accountType.setRoleList(new ArrayList<>());
        accountType.getRoleList().add(accountTypeRoleEntity);
    }
}
