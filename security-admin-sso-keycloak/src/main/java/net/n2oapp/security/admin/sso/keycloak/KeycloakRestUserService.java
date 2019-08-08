package net.n2oapp.security.admin.sso.keycloak;

import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import javax.ws.rs.core.Response;
import java.util.List;

public class KeycloakRestUserService {

    private static String USER_BY_ID = "%s/admin/realms/%s/users/%s";
    private static String USERS = "%s/admin/realms/%s/users/";

    private SsoKeycloakProperties properties;

    @Autowired
    private RestOperations template;


    public KeycloakRestUserService(SsoKeycloakProperties properties) {
        this.properties = properties;
    }

    public UserRepresentation getById(String userGuid) {
        final String serverUrl = String.format(USER_BY_ID, properties.getServerUrl(), properties.getRealm(), userGuid);
        try {
            ResponseEntity<UserRepresentation> response = template.getForEntity(serverUrl, UserRepresentation.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404){
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

    public void addUserRoles(List<RoleRepresentation> roles) {

    }

    public void deleteUserRoles(List<RoleRepresentation> roles) {

    }

    public void changePassword(String userGuid, CredentialRepresentation credentialRepresentation) {

    }

    public void executeActionsEmail(List<String> actions, String userGuid, String redirectUrl) {

    }

    public void changeActivity(UserRepresentation user) {

    }

    public void setTemplate(RestOperations template) {
        this.template = template;
    }
}
