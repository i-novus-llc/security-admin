package net.n2oapp.security.admin.sso.keycloak;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponentsBuilder;

import javax.ws.rs.core.Response;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class KeycloakRestUserService {

    private static String USER_BY_ID = "%s/admin/realms/%s/users/%s";
    private static String USERS = "%s/admin/realms/%s/users/";
    private static String USER_ROLES = "%s/admin/realms/%s/users/%s/role-mappings/realm";
    private static String EMAIL_ACTIONS = "%s/admin/realms/%s/users/%s/execute-actions-email";
    private static String RESET_PASSWORD = "%s/admin/realms/%s/users/%s/reset-password";

    private SsoKeycloakProperties properties;

    @Autowired
    private RestOperations template;

    @Autowired
    private KeycloakRestRoleService roleService;

    public KeycloakRestUserService(SsoKeycloakProperties properties) {
        this.properties = properties;
    }

    public UserRepresentation getById(String userGuid) {
        final String serverUrl = String.format(USER_BY_ID, properties.getServerUrl(), properties.getRealm(), userGuid);
        try {
            ResponseEntity<UserRepresentation> response = template.getForEntity(serverUrl, UserRepresentation.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return null;
            }
            throw ex;
        }
    }

    public String createUser(UserRepresentation user) {
        final String serverUrl = String.format(USERS, properties.getServerUrl(), properties.getRealm());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .postForEntity(serverUrl, new HttpEntity<>(user, headers), Response.class);
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            return response.getHeaders().getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        } else {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    public void updateUser(UserRepresentation user) {
        final String serverUrl = String.format(USER_BY_ID, properties.getServerUrl(), properties.getRealm(), user.getId());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .exchange(serverUrl, HttpMethod.PUT, new HttpEntity<>(user, headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    public void deleteUser(String guid) {
        final String serverUrl = String.format(USER_BY_ID, properties.getServerUrl(), properties.getRealm(), guid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .exchange(serverUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    public void addUserRoles(String userGuid, List<RoleRepresentation> roles) {
        if (roles != null && !roles.isEmpty()) {
            roles.forEach(r -> {
               if (r.getId() == null) {
                   RoleRepresentation byName = roleService.getByName(r.getName());
                   r.setId(byName.getId());
               }
            });
            final String serverUrl = String.format(USER_ROLES, properties.getServerUrl(), properties.getRealm(), userGuid);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<Response> response = template.postForEntity(serverUrl, new HttpEntity<>(roles, headers), Response.class);
            if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
                throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
            }
        }
    }

    public List<RoleRepresentation> getActualUserRoles(String userGuid) {
        final String serverUrl = String.format(USER_ROLES, properties.getServerUrl(), properties.getRealm(), userGuid);
        try {
            ResponseEntity<RoleRepresentation[]> response = template.getForEntity(serverUrl, RoleRepresentation[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return Collections.EMPTY_LIST;
            }
            throw ex;
        }
    }

    public void deleteUserRoles(String userGuid, List<RoleRepresentation> roles) {
        if (roles != null && !roles.isEmpty()) {
            final String serverUrl = String.format(USER_ROLES, properties.getServerUrl(), properties.getRealm(), userGuid);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            ResponseEntity<Response> response = template.exchange(serverUrl, HttpMethod.DELETE, new HttpEntity<>(roles, headers), Response.class);
            if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
                throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
            }
        }
    }

    public void changePassword(String userGuid, String newPassword) {
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue(newPassword);
        final String serverUrl = String.format(RESET_PASSWORD, properties.getServerUrl(), properties.getRealm(), userGuid);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template.exchange(serverUrl, HttpMethod.PUT, new HttpEntity<>(passwordCred, headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    public void executeActionsEmail(List<String> actions, String userGuid) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(String.format(EMAIL_ACTIONS, properties.getServerUrl(), properties.getRealm(), userGuid))
                .queryParam("redirect_uri", properties.getRedirectUrl())
                .queryParam("client_id", properties.getClientId());
        final String serverUrl = builder.toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .exchange(serverUrl, HttpMethod.PUT, new HttpEntity<>(actions, headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    public void setTemplate(RestOperations template) {
        this.template = template;
    }
}
