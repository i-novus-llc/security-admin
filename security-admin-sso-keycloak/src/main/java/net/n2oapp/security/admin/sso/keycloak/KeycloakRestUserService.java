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
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Сервис для создания, изменения, удаления пользователя в keycloak
 * https://www.keycloak.org/docs-api/6.0/rest-api/#_users_resource
 */
public class KeycloakRestUserService {

    private static String USER_BY_ID = "%s/admin/realms/%s/users/%s";
    private static String USERS = "%s/admin/realms/%s/users/";
    private static String USER_ROLES = "%s/admin/realms/%s/users/%s/role-mappings/realm";
    private static String EMAIL_ACTIONS = "%s/admin/realms/%s/users/%s/execute-actions-email";
    private static String RESET_PASSWORD = "%s/admin/realms/%s/users/%s/reset-password";
    private static String SEARCH_USERS = "%s/admin/realms/%s/users%s";
    private static String USERS_COUNT = "%s/admin/realms/%s/users/count";

    private AdminSsoKeycloakProperties properties;

    @Autowired
    private RestOperations template;

    @Autowired
    private KeycloakRestRoleService roleService;

    public KeycloakRestUserService(AdminSsoKeycloakProperties properties) {
        this.properties = properties;
    }

    /**
     * Возвращает количество пользователей
     */
    public Integer getUsersCount() {
        final String serverUrl = String.format(USERS_COUNT, properties.getServerUrl(), properties.getRealm());
        try {
            return template.getForObject(serverUrl, Integer.class);
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return null;
            }
            throw ex;
        }
    }

    /**
     * Получение пользователя по guid
     * @param userGuid  guid пользователя
     * @return          данные пользователя
     */
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

    /**
     * Поиск пользователей
     * @param search - строка поиска (A String contained in username, first or last name, or email)
     * @param first  - с какого пользователя начать
     * @param max    - сколько пользователей вернуть
     * @return -лист пользователей
     */
    public List<UserRepresentation> searchUsers(String search, Integer first, Integer max) {
        String criteria = first == null ? "" : "first=" + first;
        if (max != null) {
            criteria += criteria.isEmpty() ? "max=" + max : "&max=" + max;
        }
        if (search != null && !search.isEmpty()) {
            criteria += criteria.isEmpty() ? "search=" + search : "&search=" + search;
        }

        final String serverUrl = String.format(SEARCH_USERS, properties.getServerUrl(), properties.getRealm(), criteria.isEmpty() ? "" : "?" + criteria);
        try {
            ResponseEntity<UserRepresentation[]> response = template.getForEntity(serverUrl, UserRepresentation[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return Collections.EMPTY_LIST;
            }
            throw ex;
        }
    }

    /**
     * Добавление пользователя
     * @param user  данные пользователя
     * @return      giud пользователя в keycloak
     */
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

    /**
     * Изменение пользователя
     * @param user    данные пользователя
     */
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

    /**
     * Удаление пользователя
     * @param guid giud пользователя keycloak
     */
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

    /**
     * Добавление пользователю ролей
     * @param userGuid  giud пользователя
     * @param roles     список ролей
     */
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

    /**
     * Получение актуальных ролей пользователя
     * @param userGuid      giud пользователя
     * @return              список ролей
     */
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

    /**
     * Удаление ролей у пользователя
     * @param userGuid  guid пользователя
     * @param roles     список ролей
     */
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

    /**
     * Изменние пароля
     * @param userGuid      giud пользователя
     * @param newPassword   новый пароль
     */
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

    public void setTemplate(RestOperations template) {
        this.template = template;
    }
}
