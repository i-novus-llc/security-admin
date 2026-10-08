package net.n2oapp.security.admin.sso.keycloak;

import jakarta.ws.rs.core.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;
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

import static net.n2oapp.security.admin.sso.keycloak.KeycloakResponseUtil.getErrorMessage;
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
        ResponseEntity<Response> response;
        try {
            response = restClient.post().uri(serverUrl).contentType(APPLICATION_JSON).body(role).retrieve().toEntity(Response.class);
        } catch (RestClientResponseException e) {
            if (e.getStatusCode().equals(HttpStatus.CONFLICT)) {
                log.warn(String.format("Role with id:\'%s\' already exists in keycloak:\'%s\'", role.getName(), properties.getServerUrl()));
                return role.getName();
            } else throw e;
        }
        if (!response.getStatusCode().is2xxSuccessful())
            throw new IllegalArgumentException(getErrorMessage(response));
        if (role.getComposites() != null)
            addComposites(role);
        return getByName(role.getName()).getId();
    }

    /**
     * Добавить созданной роли составные роли
     *
     * @param role данные роли
     */
    private void addComposites(RoleRepresentation role) {
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), role.getName());
        postComposites(roleCompositesServerUrl, collectCompositeIds(role).stream().map(IdObject::new).collect(Collectors.toSet()));
    }

    /**
     * Идентификаторы составных ролей (realm и client) из данных роли
     *
     * @param role данные роли
     * @return идентификаторы составных ролей
     */
    private Set<String> collectCompositeIds(RoleRepresentation role) {
        Set<String> composites = new HashSet<>();
        if (role.getComposites().getRealm() != null) {
            composites.addAll(role.getComposites().getRealm());
        }
        if (role.getComposites().getClient() != null) {
            composites.addAll(role.getComposites().getClient().values().stream().filter(Objects::nonNull)
                    .flatMap(Collection::stream).collect(Collectors.toSet()));
        }
        return composites;
    }

    private void postComposites(String roleCompositesServerUrl, Set<IdObject> composites) {
        ResponseEntity<Response> compositesResponse = restClient.post().uri(roleCompositesServerUrl).contentType(APPLICATION_JSON).body(composites).retrieve().toEntity(Response.class);
        if (!compositesResponse.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(getErrorMessage(compositesResponse));
        }
    }

    /**
     * Изменить роль
     *
     * @param role данные роли
     */
    public void updateRole(RoleRepresentation role) {
        final String serverUrl = String.format(ROLE_BY_NAME, properties.getServerUrl(), properties.getRealm(), role.getName());
        ResponseEntity<Response> response = restClient.put().uri(serverUrl).contentType(APPLICATION_JSON).body(role).retrieve().toEntity(Response.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(getErrorMessage(response));
        }
        if (role.isComposite())
            updateComposites(role);
    }

    /**
     * Синхронизировать составные роли в keycloak с данными роли: добавить новые и удалить лишние
     *
     * @param role данные роли
     */
    private void updateComposites(RoleRepresentation role) {
        final String roleCompositesServerUrl = String.format(ROLE_COMPOSITES, properties.getServerUrl(), properties.getRealm(), role.getName());
        Set<String> currentComposites = getRoleComposites(role.getName()).stream()
                .map(RoleRepresentation::getId).collect(Collectors.toSet());
        Set<String> composites = role.getComposites() == null ? Collections.emptySet() : collectCompositeIds(role);
        if (role.getComposites() != null) {
            Set<IdObject> newComposites = composites.stream().filter(r -> !currentComposites.contains(r))
                    .map(IdObject::new).collect(Collectors.toSet());
            postComposites(roleCompositesServerUrl, newComposites);
        }
        Set<IdObject> forRemove = currentComposites.stream().filter(r -> !composites.contains(r))
                .map(IdObject::new).collect(Collectors.toSet());
        if (!forRemove.isEmpty()) {
            restClient.method(HttpMethod.DELETE).uri(roleCompositesServerUrl).contentType(APPLICATION_JSON).body(forRemove).retrieve().toEntity(Response.class);
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
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new IllegalArgumentException(getErrorMessage(response));
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
