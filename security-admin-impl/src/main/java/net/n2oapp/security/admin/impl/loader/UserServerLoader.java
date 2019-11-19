package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.impl.entity.*;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class UserServerLoader extends RepositoryServerLoader<UserForm, UserEntity, Integer> {

    public UserServerLoader(UserRepository repository) {
        super(repository, UserServerLoader::map);
    }

    private static UserEntity map(UserForm form, String subject) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(form.getId());
        userEntity.setUsername(form.getUsername());
        userEntity.setName(form.getName());
        userEntity.setSurname(form.getSurname());
        userEntity.setPatronymic(form.getPatronymic());
        userEntity.setIsActive(form.getIsActive());
        userEntity.setEmail(form.getEmail());
        userEntity.setSnils(form.getSnils());
        userEntity.setUserLevel(nonNull(form.getUserLevel()) ? UserLevel.valueOf(form.getUserLevel()) : null);
        userEntity.setDepartment(nonNull(form.getDepartmentId()) ? new DepartmentEntity(form.getDepartmentId()) : null);
        userEntity.setOrganization(nonNull(form.getOrganizationId()) ? new OrganizationEntity(form.getOrganizationId()) : null);
        userEntity.setRegion(nonNull(form.getRegionId()) ? new RegionEntity(form.getRegionId()) : null);
        if (nonNull(form.getRoles()))
            userEntity.setRoleList(form.getRoles().stream().map(RoleEntity::new).collect(Collectors.toList()));
        return userEntity;
    }

    @Override
    public String getTarget() {
        return "users";
    }

    @Override
    public Class<UserForm> getDataType() {
        return UserForm.class;
    }
}
