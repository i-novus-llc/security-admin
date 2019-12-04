package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.repository.RepositoryServerLoader;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.DepartmentEntity;
import net.n2oapp.security.admin.impl.entity.OrganizationEntity;
import net.n2oapp.security.admin.impl.entity.RegionEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Objects.nonNull;

@Component
public class UserServerLoader extends RepositoryServerLoader<User, UserEntity, Integer> {

    @Autowired
    private RoleRepository roleRepository;

    private UserRepository userRepository;

    public UserServerLoader(UserRepository repository) {
        super(repository, null);
        userRepository = repository;
    }

    protected List<UserEntity> map(List<User> data, String subject) {
        List<UserEntity> fresh = new ArrayList<>();
        for (User model : data) {
            UserEntity userEntity = new UserEntity();
            userEntity.setId(model.getId());
            userEntity.setUsername(model.getUsername());
            userEntity.setName(model.getName());
            userEntity.setSurname(model.getSurname());
            userEntity.setPatronymic(model.getPatronymic());
            userEntity.setIsActive(model.getIsActive());
            userEntity.setEmail(model.getEmail());
            userEntity.setSnils(model.getSnils());
            userEntity.setUserLevel(model.getUserLevel());
            userEntity.setDepartment(nonNull(model.getDepartment()) ? new DepartmentEntity(model.getDepartment().getId()) : null);
            userEntity.setOrganization(nonNull(model.getOrganization()) ? new OrganizationEntity(model.getOrganization().getId()) : null);
            userEntity.setRegion(nonNull(model.getRegion()) ? new RegionEntity(model.getRegion().getId()) : null);
            if (nonNull(model.getRoles()))
                userEntity.setRoleList(model.getRoles().stream().map(role -> roleRepository.findOneByCode(role.getCode())).collect(Collectors.toList()));
            fresh.add(userEntity);
        }
        return fresh;
    }

    @Override
    @Transactional
    public void load(List<User> data, String subject) {
        List<UserEntity> fresh = map(data, subject);
        save(fresh);
        delete(fresh, subject);
    }

    protected void save(List<UserEntity> fresh) {
        for (UserEntity user : fresh) {
            UserEntity entity = userRepository.findOneByUsernameIgnoreCase(user.getUsername());
            if (entity == null) {
                userRepository.save(user);
            } else user.setId(entity.getId());
        }
    }

    @Override
    public String getTarget() {
        return "users";
    }

    @Override
    public Class<User> getDataType() {
        return User.class;
    }
}
