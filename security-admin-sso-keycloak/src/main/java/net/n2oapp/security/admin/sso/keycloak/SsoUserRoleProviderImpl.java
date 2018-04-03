package net.n2oapp.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import javax.ws.rs.core.Response;

public class SsoUserRoleProviderImpl implements SsoUserRoleProvider {

    private SsoKeycloakProperties properties;

    public SsoUserRoleProviderImpl(SsoKeycloakProperties properties) {
        this.properties = properties;
    }

    @Override
    public String createUser(User user) {
        UserRepresentation userRepresentation = map(user);
        UsersResource usersResource = keycloak().realm(properties.getRealm()).users();
        Response response = usersResource.create(userRepresentation);
        if (response.getStatus() == 200 || response.getStatus() == 201) {
            String userId = response.getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
            return userId;
        } else {
            throw new IllegalArgumentException("Can't create user in keycloak!");
        }
    }

    @Override
    public void deleteUser(String userGuid) {
        keycloak().realm(properties.getRealm()).users().delete(userGuid);
    }

    @Override
    public void createRole(Role role) {
        RolesResource resource = keycloak().realm(properties.getRealm()).roles();
        RoleRepresentation roleRepresentation = map(role);
        resource.create(roleRepresentation);
    }

    @Override
    public void deleteRole(String roleName) {
        keycloak().realm(properties.getRealm()).roles().deleteRole(roleName);
    }

    private Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(properties.getServerUrl())
                .realm(properties.getRealm())
                .clientId(properties.getClientId())
                .username(properties.getUsername())
                .password(properties.getPassword())
                .build();
    }

    private UserRepresentation map(User user) {
        UserRepresentation kUser = new UserRepresentation();
        kUser.setId(user.getGuid());
        kUser.setEnabled(true);
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
