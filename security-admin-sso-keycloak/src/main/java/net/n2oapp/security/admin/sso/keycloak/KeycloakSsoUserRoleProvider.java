package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;

public class KeycloakSsoUserRoleProvider implements SsoUserRoleProvider {

    private SsoKeycloakProperties properties;

    @Autowired
    private KeycloakRestRoleService roleService;

    @Autowired
    private KeycloakRestUserService userService;

    public KeycloakSsoUserRoleProvider(SsoKeycloakProperties properties) {
        this.properties = properties;
    }

    @Override
    public User createUser(User user) {
        UserRepresentation userRepresentation = map(user);
        String userGuid = userService.createUser(userRepresentation);
        user.setGuid(userGuid);
        if (user.getRoles() != null) {
            List<RoleRepresentation> roles = new ArrayList<>();
            List<RoleRepresentation> roleRepresentationList = roleService.getAllRoles();
            user.getRoles().forEach(r -> {
                Optional<RoleRepresentation> roleRep = roleRepresentationList.stream().filter(rp -> rp.getName().equals(r.getCode())).findAny();
                if (roleRep.isPresent()) {
                    roles.add(roleRep.get());
                } else {
                    throw new UserException("exception.ssoRoleNotFound");
                }
            });
            userService.addUserRoles(userGuid, roles);
        }
        if (properties.getSendVerifyEmail() || properties.getSendChangePassword()) {
            List<String> actions = new ArrayList<>();
            if (properties.getSendVerifyEmail()) {
                actions.add("VERIFY_EMAIL");
            }
            if (properties.getSendChangePassword()) {
                actions.add("UPDATE_PASSWORD");
            }
            userService.executeActionsEmail(actions, userGuid);
        }
        return user;
    }

    @Override
    public void updateUser(User user) {
        UserRepresentation userRepresentation = map(user);
        userService.updateUser(userRepresentation);
        List<RoleRepresentation> forRemove = new ArrayList<>();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            forRemove = userService.getActualUserRoles(user.getGuid());
        } else {
            Set<String> roleNames = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
            List<RoleRepresentation> effective = userService.getActualUserRoles(user.getGuid());
            if (effective != null) {
                forRemove = effective.stream().filter(e -> !roleNames.contains(e.getName())).collect(Collectors.toList());
            }
            Set<String> effectiveRoleNames = effective == null ? new HashSet<>() :
                    effective.stream().map(r -> r.getName()).collect(Collectors.toSet());
            userService.addUserRoles(user.getGuid(), user.getRoles().stream()
                    .filter(r -> !effectiveRoleNames.contains(r.getCode())).map(r -> map(r)).collect(Collectors.toList()));
        }
        userService.deleteUserRoles(user.getGuid(), forRemove);
        if (user.getPassword() != null) {
            userService.changePassword(user.getGuid(), user.getPassword());
        }
    }

    @Override
    public void deleteUser(User user) {
        userService.deleteUser(user.getGuid());
    }

    @Override
    public void changeActivity(User user) {
        UserRepresentation userRepresentation = map(user);
        userRepresentation.setEnabled(user.getIsActive());
        userService.updateUser(userRepresentation);
    }

    @Override
    public Role createRole(Role role) {
        roleService.createRole(map(role));
        return role;
    }

    @Override
    public void updateRole(Role role) {
        roleService.updateRole(map(role));
    }

    @Override
    public void deleteRole(Role role) {
        roleService.deleteRole(role.getCode());
    }

    private UserRepresentation map(User user) {
        UserRepresentation kUser = new UserRepresentation();
        kUser.setId(user.getGuid());
        kUser.setEnabled(user.getIsActive());
        kUser.setUsername(user.getUsername());
        kUser.setFirstName(user.getName());
        kUser.setLastName(user.getSurname());
        kUser.setEmail(user.getEmail());
        if (user.getPassword() != null) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(user.getPassword());
            kUser.setCredentials(Arrays.asList(passwordCred));
        }
        return kUser;
    }

    private RoleRepresentation map(Role role) {
        RoleRepresentation res = new RoleRepresentation();
        res.setName(role.getCode());
        res.setComposite(false);
        res.setDescription(role.getDescription());
        return res;
    }

    public void setRoleService(KeycloakRestRoleService roleService) {
        this.roleService = roleService;
    }

    public void setUserService(KeycloakRestUserService userService) {
        this.userService = userService;
    }
}
