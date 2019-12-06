package net.n2oapp.security.admin.sso.keycloak;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import javax.ws.rs.core.Response;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Сервис для создания, изменения, удаления ролей в keycloak
 */
public class KeycloakRestRoleService {

    private static String ROLE_BY_NAME = "%s/admin/realms/%s/roles/%s";
    private static String ROLES = "%s/admin/realms/%s/roles/";
    private static String ROLE_COMPOSITES = "%s/admin/realms/%s/roles/%s/composites";

    private AdminSsoKeycloakProperties properties;
    private RestOperations template;

    public KeycloakRestRoleService(AdminSsoKeycloakProperties properties, RestOperations template) {
        this.properties = properties;
        this.template = template;
    }

    public void setTemplate(RestOperations template) {
        this.template = template;
    }

    /**
     * Получение роли по уникальному имени(коду)
     *
     * @param roleName имя роля
     * @return роль
     */
    public RoleRepresentation getByName(String roleName) {
        final String serverUrl = String.format(ROLE_BY_NAME, properties.getServerUrl(), properties.getRealm(), roleName);
        try {
            ResponseEntity<RoleRepresentation> response = template.getForEntity(serverUrl, RoleRepresentation.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return null;
            }
            throw ex;
        }
    }

    /**
     * Получение всех ролей realm уровня из keycloak
     *
     * @return список ролей
     */
    public List<RoleRepresentation> getAllRoles() {
        final String serverUrl = String.format(ROLES, properties.getServerUrl(), properties.getRealm());
        try {
            ResponseEntity<RoleRepresentation[]> response = template.getForEntity(serverUrl, RoleRepresentation[].class);
            return Arrays.asList(response.getBody());
        } catch (HttpClientErrorException ex) {
            if (ex.getRawStatusCode() == 404) {
                return Collections.emptyList();
            }
            throw ex;
        }
    }

    /**
     * Создать роль
     *
     * @param role данные новой роли
     * @return идентификатор новой роли
     */
    public String createRole(RoleRepresentation role) {
        final String serverUrl = String.format(ROLES, properties.getServerUrl(), properties.getRealm());
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), role.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .postForEntity(serverUrl, new HttpEntity<>(role, headers), Response.class);
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            if (role.getComposites() != null) {
                Set<IdObject> composites = new HashSet<>();
                if (role.getComposites().getRealm() != null) {
                    composites.addAll(role.getComposites().getRealm().stream().map(r -> new IdObject(r)).collect(Collectors.toSet()));
                }
                if (role.getComposites().getClient() != null) {
                    composites.addAll(role.getComposites().getClient().values().stream().filter(r -> r != null)
                            .flatMap(r -> r.stream()).map(r -> new IdObject(r)).collect(Collectors.toSet()));
                }
                ResponseEntity<Response> compositesResponse = template
                        .postForEntity(roleCompositesServerUrl, new HttpEntity<>(composites, headers), Response.class);
                if (compositesResponse.getStatusCodeValue() < 200 || compositesResponse.getStatusCodeValue() > 300) {
                    throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
                }
            }
            return response.getHeaders().getLocation().getPath().replaceAll(".*/([^/]+)$", "$1");
        } else {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    /**
     * Изменить роль
     *
     * @param role данные роли
     */
    public void updateRole(RoleRepresentation role) {
        final String serverUrl = String.format(ROLE_BY_NAME, properties.getServerUrl(), properties.getRealm(), role.getName());
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), role.getName());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .exchange(serverUrl, HttpMethod.PUT, new HttpEntity<>(role, headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
        if (response.getStatusCodeValue() >= 200 && response.getStatusCodeValue() < 300) {
            if (role.isComposite()) {
                RoleRepresentation[] currentCompositesRes = getRoleComposites(role.getName());
                Set<String> currentComposites = new HashSet<>();
                for (RoleRepresentation r : currentCompositesRes) {
                    currentComposites.add(r.getId());
                }
                Set<IdObject> forRemove;
                if (role.getComposites() == null) {
                    forRemove = currentComposites.stream().map(r -> new IdObject(r)).collect(Collectors.toSet());
                } else {
                    Set<String> composites = new HashSet<>();
                    if (role.getComposites().getRealm() != null) {
                        composites.addAll(role.getComposites().getRealm());
                    }
                    if (role.getComposites().getClient() != null) {
                        composites.addAll(role.getComposites().getClient().values().stream().filter(r -> r != null)
                                .flatMap(r -> r.stream()).collect(Collectors.toSet()));
                    }
                    forRemove = currentComposites.stream().filter(r -> !composites.contains(r)).map(r -> new IdObject(r)).collect(Collectors.toSet());
                    Set<IdObject> newComposites = composites.stream().filter(r -> !currentComposites.contains(r))
                            .map(r -> new IdObject(r)).collect(Collectors.toSet());
                    if (newComposites != null) {
                        ResponseEntity<Response> compositesResponse = template
                                .postForEntity(roleCompositesServerUrl, new HttpEntity<>(newComposites, headers), Response.class);
                        if (compositesResponse.getStatusCodeValue() < 200 || compositesResponse.getStatusCodeValue() > 300) {
                            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
                        }
                    }
                }
                if (!forRemove.isEmpty()) {
                    template.exchange(roleCompositesServerUrl, HttpMethod.DELETE, new HttpEntity<>(forRemove, headers), Response.class);
                }
            }
        } else {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    /**
     * Получение содержимого роли, в случае если она композитная
     *
     * @param roleName имя роли
     * @return список ролей
     */
    public RoleRepresentation[] getRoleComposites(String roleName) {
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), roleName);
        return template.getForEntity(roleCompositesServerUrl, RoleRepresentation[].class).getBody();
    }

    /**
     * Удаление роли
     *
     * @param roleName имя роли
     */
    public void deleteRole(String roleName) {
        final String serverUrl = String.format(ROLE_BY_NAME, properties.getServerUrl(), properties.getRealm(), roleName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<Response> response = template
                .exchange(serverUrl, HttpMethod.DELETE, new HttpEntity<>(headers), Response.class);
        if (response.getStatusCodeValue() < 200 || response.getStatusCodeValue() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    static class IdObject {
        private String id;
    }
}
