package net.n2oapp.security.admin.impl.service;

import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.impl.entity.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.StringJoiner;
import java.util.stream.Collectors;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.springframework.util.StringUtils.hasText;

@Component
public class Mapper {

    public SsoUser model(UserEntity entity) {
        return model(entity, false);
    }

    public SsoUser model(UserEntity entity, Boolean withAccount) {
        if (isNull(entity)) return null;
        SsoUser model = new SsoUser();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        model.setSnils(entity.getSnils());
        model.setExpirationDate(entity.getExpirationDate());
        model.setRegion(model(entity.getRegion()));

        StringJoiner joiner = new StringJoiner(" ");
        if (nonNull(entity.getSurname()))
            joiner.add(entity.getSurname());
        if (nonNull(entity.getName()))
            joiner.add(entity.getName());
        if (nonNull(entity.getPatronymic()))
            joiner.add(entity.getPatronymic());
        String fio = joiner.toString();
        model.setFio(hasText(fio) ? fio : null);

        if (Boolean.TRUE.equals(withAccount) && entity.getAccounts() != null) {
            model.setAccounts(new ArrayList<>());
            entity.getAccounts().forEach(a -> model.getAccounts().add(model(a)));
        }

        return model;
    }

    public Region model(RegionEntity entity) {
        if (isNull(entity)) return null;
        Region region = new Region();
        region.setId(entity.getId());
        region.setName(entity.getName());
        region.setCode(entity.getCode());
        region.setOkato(entity.getOkato());
        return region;
    }

    public Account model(AccountEntity entity) {
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

    public Role model(RoleEntity entity) {
        if (isNull(entity)) return null;
        Role model = new Role();
        model.setId(entity.getId());
        model.setCode(entity.getCode());
        model.setName(entity.getName());
        model.setDescription(entity.getDescription());
        model.setNameWithSystem(entity.getName());
        if (nonNull(entity.getSystem()))
            model.setNameWithSystem(model.getNameWithSystem() + "(" + entity.getSystem().getName() + ")");

        return model;
    }

    public Department model(DepartmentEntity entity) {
        if (entity == null) return null;
        Department model = new Department();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setCode(entity.getCode());
        return model;
    }

    public Organization model(OrganizationEntity entity) {
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

}
