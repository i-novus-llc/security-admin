package net.n2oapp.security.admin.sso.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
import org.keycloak.representations.idm.ErrorRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientResponseException;

import java.util.*;
import java.util.stream.Collectors;

import static org.springframework.http.MediaType.APPLICATION_JSON;

/**
 * Сервис для создания, изменения, удаления ролей в keycloak
 */
@Log4j2
public class KeycloakRestRoleService {
    private static String ROLE_BY_NAME = "%s/admin/realms/%s/roles/%s";
    private static String ROLES = "%s/admin/realms/%s/roles/";
    private static String ROLE_COMPOSITES = "%s/admin/realms/%s/roles/%s/composites";

    private AdminSsoKeycloakProperties properties;
    private RestClient restClient;

    public KeycloakRestRoleService(AdminSsoKeycloakProperties properties, RestClient restClient) {
        this.properties = properties;
        this.restClient = restClient;
    }

    public void setRestClient(RestClient restClient) {
        this.restClient = restClient;
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
            ResponseEntity<RoleRepresentation> response = restClient.get().uri(serverUrl).retrieve().toEntity(RoleRepresentation.class);
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
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
            ResponseEntity<List<RoleRepresentation>> response = restClient.get().uri(serverUrl).retrieve().toEntity(new ParameterizedTypeReference<List<RoleRepresentation>>(){});
            return response.getBody();
        } catch (HttpClientErrorException ex) {
            if (ex.getStatusCode().value() == 404) {
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
        ResponseEntity<Response> response;
        try {
            response = restClient.post().uri(serverUrl).contentType(APPLICATION_JSON).body(role).retrieve().toEntity(Response.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                log.warn(String.format("Role with id:\'%s\' already exists in keycloak:\'%s\'", role.getName(), properties.getServerUrl()));
                return role.getName();
            } else throw e;
        }
        if (response.getStatusCode().value() >= 200 && response.getStatusCode().value() < 300) {
            if (role.getComposites() != null) {
                Set<IdObject> composites = new HashSet<>();
                if (role.getComposites().getRealm() != null) {
                    composites.addAll(role.getComposites().getRealm().stream().map(IdObject::new).collect(Collectors.toSet()));
                }
                if (role.getComposites().getClient() != null) {
                    composites.addAll(role.getComposites().getClient().values().stream().filter(Objects::nonNull)
                            .flatMap(Collection::stream).map(IdObject::new).collect(Collectors.toSet()));
                }
                ResponseEntity<Response> compositesResponse = restClient.post().uri(roleCompositesServerUrl).contentType(APPLICATION_JSON).body(composites).retrieve().toEntity(Response.class);
                if (compositesResponse.getStatusCode().value() < 200 || compositesResponse.getStatusCode().value() > 300) {
                    throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
                }
            }
            return getByName(role.getName()).getId();
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
        ResponseEntity<Response> response = restClient.put().uri(serverUrl).contentType(APPLICATION_JSON).body(role).retrieve().toEntity(Response.class);
        if (response.getStatusCode().value() < 200 || response.getStatusCode().value() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
        if (response.getStatusCode().value() >= 200 && response.getStatusCode().value() < 300) {
            if (role.isComposite()) {
                List<RoleRepresentation> currentCompositesRes = getRoleComposites(role.getName());
                Set<String> currentComposites = new HashSet<>();
                for (RoleRepresentation r : currentCompositesRes) {
                    currentComposites.add(r.getId());
                }
                Set<IdObject> forRemove;
                if (role.getComposites() == null) {
                    forRemove = currentComposites.stream().map(IdObject::new).collect(Collectors.toSet());
                } else {
                    Set<String> composites = new HashSet<>();
                    if (role.getComposites().getRealm() != null) {
                        composites.addAll(role.getComposites().getRealm());
                    }
                    if (role.getComposites().getClient() != null) {
                        composites.addAll(role.getComposites().getClient().values().stream().filter(Objects::nonNull)
                                .flatMap(Collection::stream).collect(Collectors.toSet()));
                    }
                    forRemove = currentComposites.stream().filter(r -> !composites.contains(r)).map(IdObject::new).collect(Collectors.toSet());
                    Set<IdObject> newComposites = composites.stream().filter(r -> !currentComposites.contains(r))
                            .map(IdObject::new).collect(Collectors.toSet());
                    if (newComposites != null) {
                        ResponseEntity<Response> compositesResponse = restClient.post().uri(roleCompositesServerUrl).contentType(APPLICATION_JSON).body(newComposites).retrieve().toEntity(Response.class);
                        if (compositesResponse.getStatusCode().value() < 200 || compositesResponse.getStatusCode().value() > 300) {
                            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
                        }
                    }
                }
                if (!forRemove.isEmpty()) {
                    restClient.method(HttpMethod.DELETE).uri(roleCompositesServerUrl).contentType(APPLICATION_JSON).body(forRemove).retrieve().toEntity(Response.class);
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
    public List<RoleRepresentation> getRoleComposites(String roleName) {
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), roleName);
        return restClient.get().uri(roleCompositesServerUrl).retrieve().toEntity(new ParameterizedTypeReference<List<RoleRepresentation>>(){}).getBody();
    }

    /**
     * Удаление роли
     *
     * @param roleName имя роли
     */
    public void deleteRole(String roleName) {
        final String serverUrl = String.format(ROLE_BY_NAME, properties.getServerUrl(), properties.getRealm(), roleName);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(APPLICATION_JSON);
        ResponseEntity<Response> response = restClient.method(HttpMethod.DELETE).uri(serverUrl).contentType(APPLICATION_JSON).retrieve().toEntity(Response.class);
        if (response.getStatusCode().value() < 200 || response.getStatusCode().value() > 300) {
            throw new IllegalArgumentException(response.getBody().readEntity(ErrorRepresentation.class).getErrorMessage());
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class IdObject {
        private String id;
    }
}
