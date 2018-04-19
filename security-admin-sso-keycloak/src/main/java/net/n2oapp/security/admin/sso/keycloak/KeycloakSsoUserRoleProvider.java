package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.NotFoundException;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class KeycloakSsoUserRoleProvider implements SsoUserRoleProvider {

    private SsoKeycloakProperties properties;

    public KeycloakSsoUserRoleProvider(SsoKeycloakProperties properties) {
        this.properties = properties;
    }

    @Override
    public User createUser(User user) {
        UserRepresentation userRepresentation = map(user);
        if (user.getPassword() != null) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(user.getPassword());
            userRepresentation.setCredentials(Arrays.asList(passwordCred));
        }
        RealmResource realmResource = keycloak().realm(properties.getRealm());
        Response response = realmResource.users().create(userRepresentation);
        if (response.getStatus() == 200 || response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            user.setGuid(userId);
            if (user.getRoles() != null) {
                List<RoleRepresentation> roles = new ArrayList<>();
                user.getRoles().forEach(r -> {
                    roles.add(realmResource.roles().get(r.getCode()).toRepresentation());
                });
                realmResource.users().get(userId).roles().realmLevel().add(roles);
            }
            if (properties.getSendVerifyEmail()) {
                realmResource.users().get(userId).executeActionsEmail(properties.getClientId(), properties.getRedirectUrl(), Arrays.asList("UPDATE_PASSWORD", "VERIFY_EMAIL"));
            }
            return user;
        } else {
            throw new IllegalArgumentException("Can't create user in keycloak!");
        }
    }

    @Override
    public void updateUser(User user) {
        RealmResource realmResource = keycloak().realm(properties.getRealm());
        UserRepresentation userRepresentation = map(user);
        realmResource.users().get(user.getGuid()).update(userRepresentation);
        List<RoleRepresentation> forRemove = new ArrayList<>();
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            forRemove = realmResource.users().get(user.getGuid()).roles().realmLevel().listEffective();
        } else {
            Set<String> roleNames = user.getRoles().stream().map(Role::getCode).collect(Collectors.toSet());
            List<RoleRepresentation> effective = realmResource.users().get(user.getGuid()).roles().realmLevel().listEffective();
            if (effective != null || !effective.isEmpty()) {
                forRemove = effective.stream().filter(e -> !roleNames.contains(e.getName())).collect(Collectors.toList());
            }
            List<RoleRepresentation> roles = new ArrayList<>();
            roleNames.forEach(r -> {
                roles.add(realmResource.roles().get(r).toRepresentation());
            });
            realmResource.users().get(user.getGuid()).roles().realmLevel().add(roles);
        }
        realmResource.users().get(user.getGuid()).roles().realmLevel().remove(forRemove);
        if (user.getPassword() != null) {
            CredentialRepresentation passwordCred = new CredentialRepresentation();
            passwordCred.setTemporary(false);
            passwordCred.setType(CredentialRepresentation.PASSWORD);
            passwordCred.setValue(user.getPassword());
            realmResource.users().get(user.getGuid()).resetPassword(passwordCred);
        }
    }

    @Override
    public void deleteUser(User user) {
        keycloak().realm(properties.getRealm()).users().delete(user.getGuid());
    }

    @Override
    public void changeActivity(User user) {
        UserRepresentation userRepresentation = map(user);
        userRepresentation.setEnabled(user.getIsActive());
        keycloak().realm(properties.getRealm()).users().get(user.getGuid()).update(userRepresentation);
    }

    @Override
    public Role createRole(Role role) {
        RolesResource resource = keycloak().realm(properties.getRealm()).roles();
        RoleRepresentation roleRepresentation = map(role);
        resource.create(roleRepresentation);
        return role;
    }

    @Override
    public void updateRole(Role role) {
        RolesResource resource = keycloak().realm(properties.getRealm()).roles();
        try {
            resource.get(role.getCode()).toRepresentation();
        } catch (NotFoundException e) {
            RoleRepresentation roleRepresentation = map(role);
            resource.create(roleRepresentation);
        }
    }

    @Override
    public void deleteRole(Role role) {
        keycloak().realm(properties.getRealm()).roles().deleteRole(role.getCode());
    }

    private Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getAdminClientId())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    private UserRepresentation map(User user) {
        UserRepresentation kUser = new UserRepresentation();
        kUser.setId(user.getGuid());
        kUser.setEnabled(user.getIsActive());
        kUser.setUsername(user.getUsername());
        kUser.setFirstName(user.getName());
        kUser.setLastName(user.getSurname());
        kUser.setEmail(user.getEmail());
        return kUser;
    }

    private RoleRepresentation map(Role role) {
        RoleRepresentation res = new RoleRepresentation();
        res.setName(role.getCode());
        res.setComposite(false);
        res.setDescription(role.getDescription());
        return res;
    }
}
