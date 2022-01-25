package net.n2oapp.security.admin.impl.loader;

import net.n2oapp.platform.loader.server.ServerLoader;
import net.n2oapp.platform.loader.server.ServerLoaderSettings;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class UserServerLoader extends ServerLoaderSettings<User> implements ServerLoader<User> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public void load(List<User> data, String subject) {
        List<UserEntity> fresh = map(data);
        if (isCreateRequired())
            userRepository.saveAll(fresh);
    }

    private List<UserEntity> map(List<User> uploadedUsers) {
        List<UserEntity> freshUsers = new ArrayList<>();
        List<UserEntity> oldUsers = userRepository.findByUsernameIn(uploadedUsers.stream().map(user -> user.getUsername()).collect(Collectors.toList()));
        for (User model : uploadedUsers) {
            UserEntity oldUser = oldUsers.stream().filter(userEntity -> userEntity.getUsername().equals(model.getUsername())).findFirst().orElse(null);
            UserEntity userEntity = new UserEntity();
            if (oldUser != null) {
                userEntity = oldUser;
            }
            userEntity.setUsername(model.getUsername());
            userEntity.setName(model.getName());
            userEntity.setSurname(model.getSurname());
            userEntity.setPatronymic(model.getPatronymic());
            userEntity.setIsActive(model.getIsActive());
            userEntity.setEmail(model.getEmail());
            userEntity.setSnils(model.getSnils());
//            todo SECURITY-396
//            userEntity.setUserLevel(model.getUserLevel());
//            userEntity.setDepartment(nonNull(model.getDepartment()) ? new DepartmentEntity(model.getDepartment().getId()) : null);
//            userEntity.setOrganization(nonNull(model.getOrganization()) ? new OrganizationEntity(model.getOrganization().getId()) : null);
//            userEntity.setRegion(nonNull(model.getRegion()) ? new RegionEntity(model.getRegion().getId()) : null);
//            if (nonNull(model.getRoles()))
//                userEntity.setRoleList(model.getRoles().stream().map(role -> roleRepository.findOneByCode(role.getCode())).collect(Collectors.toList()));
            freshUsers.add(userEntity);
        }
        return freshUsers;
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
