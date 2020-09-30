package net.n2oapp.security.admin.sso.keycloak;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class KeycloakSsoUserRoleProvider implements SsoUserRoleProvider {

    public static final String EXT_SYS = "KEYCLOAK";

    private AdminSsoKeycloakProperties properties;

    @Autowired
    private KeycloakRestRoleService roleService;

    @Autowired
    private KeycloakRestUserService userService;

    @Autowired
    private SchedulerFactoryBean schedulerFactoryBean;

    @Autowired
    private ObjectMapper objectMapper;


    public KeycloakSsoUserRoleProvider(AdminSsoKeycloakProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean isSupports(String ssoName) {
        return ssoName == null || EXT_SYS.equalsIgnoreCase(ssoName);
    }

    @Override
    public SsoUser createUser(SsoUser user) {
        UserRepresentation userRepresentation = map(user);
        if (!CollectionUtils.isEmpty(user.getRequiredActions())) {
            userRepresentation.setRequiredActions(user.getRequiredActions());
        }

        try {
            String userGuid = userService.createUser(userRepresentation);
            user.setExtUid(userGuid);
            user.setExtSys("KEYCLOAK");
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
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
        return user;
    }

    @Override
    public void updateUser(SsoUser user) {
        UserRepresentation userRepresentation = map(user);
        try {
            userService.updateUser(userRepresentation);
            List<RoleRepresentation> forRemove = new ArrayList<>();
            if (user.getRoles() == null || user.getRoles().isEmpty()) {
                forRemove = userService.getActualUserRoles(user.getExtUid());
            } else {
                Set<String> roleNames = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
                List<RoleRepresentation> effective = userService.getActualUserRoles(user.getExtUid());
                if (effective != null) {
                    forRemove = effective.stream().filter(e -> !roleNames.contains(e.getName())).collect(Collectors.toList());
                }
                Set<String> effectiveRoleNames = effective == null ? new HashSet<>() :
                        effective.stream().map(RoleRepresentation::getName).collect(Collectors.toSet());
                userService.addUserRoles(user.getExtUid(), user.getRoles().stream()
                        .filter(r -> !effectiveRoleNames.contains(r.getCode())).map(this::map).collect(Collectors.toList()));
            }
            userService.deleteUserRoles(user.getExtUid(), forRemove);
            if (user.getPassword() != null) {
                userService.changePassword(user.getExtUid(), user.getPassword());
            }
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public void deleteUser(SsoUser user) {
        try {
            userService.deleteUser(user.getExtUid());
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public void changeActivity(SsoUser user) {
        UserRepresentation userRepresentation = map(user);
        userRepresentation.setEnabled(user.getIsActive());
        try {
            userService.updateUser(userRepresentation);
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public Role createRole(Role role) {
        try {
            roleService.createRole(map(role));
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
        return role;
    }

    @Override
    public void updateRole(Role role) {
        try {
            roleService.updateRole(map(role));
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public void deleteRole(Role role) {
        try {
            roleService.deleteRole(role.getCode());
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public void resetPassword(SsoUser user) {
        UserRepresentation userRepresentation = map(user);
        if (!CollectionUtils.isEmpty(user.getRequiredActions())) {
            userRepresentation.setRequiredActions(user.getRequiredActions());
        }

        try {
            userService.updateUser(userRepresentation);
        } catch (HttpClientErrorException e) {
            throwUserException(e);
        }
    }

    @Override
    public void startSynchronization() {
        try {
            schedulerFactoryBean.getScheduler().triggerJob(new JobKey(SsoKeycloakConfiguration.USER_SYNCHRONIZE_JOB_DETAIL));
        } catch (SchedulerException e) {
            throw new UserException("exception.failedSyncStart", e);
        }
    }

    private void throwUserException(HttpClientErrorException exception) {
        try {
            Map<String, String> map = objectMapper.readValue(exception.getResponseBodyAsString(), new TypeReference<Map<String, String>>() {});
            throw new UserException("exception." + map.get("errorMessage").toLowerCase().replaceAll(" ", "-"));
        } catch (IOException e) {
            throw new IllegalArgumentException(exception);
        }
    }

    private UserRepresentation map(SsoUser user) {
        UserRepresentation kUser = new UserRepresentation();
        kUser.setId(user.getExtUid());
        kUser.setEnabled(user.getIsActive());
        kUser.setUsername(user.getUsername() != null ? user.getUsername() : "");
        kUser.setFirstName(user.getName() != null ? user.getName() : "");
        kUser.setLastName(user.getSurname() != null ? user.getSurname() : "");
        kUser.setEmail(user.getEmail() != null ? user.getEmail() : "");
        kUser.setEmailVerified(properties.getEmailVerified());
        if (user.getPassword() != null) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(properties.getTemporaryPassword());
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(user.getPassword());
            kUser.setCredentials(Arrays.asList(passwordCred));
        }
        return kUser;
    }

    private Role mapRoleRepresentation(RoleRepresentation roleRepresentation) {
        if (roleRepresentation == null)
            return null;
        Role role = new Role();
        role.setCode(roleRepresentation.getName());
        role.setDescription(roleRepresentation.getDescription());
        return role;
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
