package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.transaction.annotation.Transactional;

import static java.util.Objects.isNull;


@Transactional
public class EsiaUserDetailsService extends UserDetailsServiceImpl {

    private Boolean synchronizeFio;

    @Autowired
    private SsoUserRoleProvider keycloakSsoUserRoleProvider;

    @Override
    public User loadUserDetails(UserDetailsToken userDetails) throws AuthenticationException {
        UserEntity userEntity = userRepository.findOneBySnilsIgnoreCase(userDetails.getUsername()).orElse(null);

        if (isNull(userEntity)) {
            throw new UserNotFoundAuthenticationException("User " + userDetails.getName() + " " + userDetails.getSurname() + " doesn't registered in system");
        }

        if (synchronizeFio) {
            userEntity.setName(userDetails.getName());
            userEntity.setSurname(userDetails.getSurname());
            userEntity.setPatronymic(userDetails.getPatronymic());
            keycloakSsoUserRoleProvider.updateUser(model(userRepository.save(userEntity)));
        }
        return model(userEntity);
    }

    private SsoUser model(UserEntity entity) {
        if (entity == null) return null;
        SsoUser model = new SsoUser();
        model.setId(entity.getId());
        model.setUsername(entity.getUsername());
        model.setName(entity.getName());
        model.setSurname(entity.getSurname());
        model.setPatronymic(entity.getPatronymic());
        model.setIsActive(entity.getIsActive());
        model.setEmail(entity.getEmail());
        //        todo SECURITY-396
//        model.setExtUid(entity.getExtUid());
//        model.setExtSys(entity.getExtSys());
        StringBuilder builder = new StringBuilder();
        if (entity.getSurname() != null) {
            builder.append(entity.getSurname()).append(" ");
        }
        if (entity.getName() != null) {
            builder.append(entity.getName()).append(" ");
        }
        if (entity.getPatronymic() != null) {
            builder.append(entity.getPatronymic());
        }
        model.setFio(builder.toString());
        //        todo SECURITY-396
//        if (entity.getRoleList() != null) {
//            model.setRoles(entity.getRoleList().stream().map(this::model).collect(Collectors.toList()));
//        }

        model.setExpirationDate(entity.getExpirationDate());

        return model;
    }

    public void setSynchronizeFio(Boolean synchronizeFio) {
        this.synchronizeFio = synchronizeFio;
    }
}
