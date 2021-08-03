package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestApplication.class,
        properties = {"spring.liquibase.enabled=false",
                "audit.service.url=Mocked", "audit.client.enabled=false"})
public class KeycloakRestRoleServiceTest {

    public static final String ROLES = "http://127.0.0.1:8085/auth/admin/realms/master/roles/";
    public static final String COMPOSITES = "http://127.0.0.1:8085/auth/admin/realms/master/roles/test.role/composites";

    @Spy
    private AdminSsoKeycloakProperties properties;
    @Mock
    private RestTemplate restTemplate;
    @InjectMocks
    private KeycloakRestRoleService roleService = new KeycloakRestRoleService(properties, restTemplate);

    @Test
    public void testCreateCompositesRole() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("PathMock"));
        ResponseEntity<ResponseImpl> responseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.OK);
        Mockito.doReturn(responseEntity).when(restTemplate).postForEntity(eq(ROLES), any(), eq(Response.class));
        Mockito.doReturn(responseEntity).when(restTemplate).postForEntity(eq(COMPOSITES), any(), eq(Response.class));
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId("testId");
        ResponseEntity<RoleRepresentation> representationResponseEntity = new ResponseEntity<>(roleRepresentation, HttpStatus.OK);
        String roleName = "test.role";
        Mockito.doReturn(representationResponseEntity).when(restTemplate).getForEntity(eq(ROLES + roleName), eq(RoleRepresentation.class));
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);


        RoleRepresentation role = new RoleRepresentation();
        role.setName(roleName);
        role.setComposite(true);
        role.setDescription("test composite role");
        RoleRepresentation.Composites composites = new RoleRepresentation.Composites();
        composites.setRealm(Collections.singleton("master"));
        Map<String, List<String>> clients = new HashMap<>();
        clients.put("testClient", Collections.singletonList("testClient"));
        composites.setClient(clients);
        role.setComposites(composites);

        roleService.createRole(role);

        Mockito.verify(restTemplate).postForEntity(
                Mockito.eq(ROLES),
                Mockito.any(), Mockito.eq(Response.class));

        Mockito.verify(restTemplate).postForEntity(
                Mockito.eq(COMPOSITES),
                httpEntityCaptor.capture(), Mockito.eq(Response.class));

        HttpEntity httpEntity = httpEntityCaptor.getValue();
        Set<KeycloakRestRoleService.IdObject> body = (Set<KeycloakRestRoleService.IdObject>) httpEntity.getBody();
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("master")));
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("testClient")));
    }

    @Test
    public void testUpdateRoleRemoveCompositeRole() {
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("PathMock"));
        ResponseEntity<ResponseImpl> responseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.OK);

        ResponseEntity roleRepresentationsResponse = mock(ResponseEntity.class);

        RoleRepresentation[] roleRepresentations = new RoleRepresentation[1];
        RoleRepresentation role = new RoleRepresentation();
        role.setId(UUID.randomUUID().toString());
        role.setName("test.role");
        role.setComposite(true);
        roleRepresentations[0] = role;

        doReturn(responseEntity).when(restTemplate).exchange(eq(ROLES + "test.role"), eq(HttpMethod.PUT), any(), eq(Response.class));
        doReturn(roleRepresentationsResponse).when(restTemplate).getForEntity(eq(COMPOSITES), any());
        doReturn(roleRepresentations).when(roleRepresentationsResponse).getBody();
        doReturn(responseEntity).when(restTemplate).exchange(eq(ROLES + "test.role"), eq(HttpMethod.DELETE), any(), eq(Response.class));

        roleService.updateRole(role);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(ROLES + "test.role"),
                Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class), Mockito.eq(Response.class));

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(COMPOSITES),
                Mockito.eq(HttpMethod.DELETE), httpEntityCaptor.capture(), Mockito.eq(Response.class));

        HttpEntity httpEntity = httpEntityCaptor.getValue();
        Set<KeycloakRestRoleService.IdObject> body = (Set<KeycloakRestRoleService.IdObject>) httpEntity.getBody();
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals(role.getId())));

        roleService.deleteRole(role.getName());
    }

    @Test
    public void testUpdateRoleAddCompositeRole() {
        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create("PathMock"));
        ResponseEntity<ResponseImpl> responseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.OK);

        ResponseEntity roleRepresentationsResponse = mock(ResponseEntity.class);

        RoleRepresentation roleToUpdate = new RoleRepresentation();
        roleToUpdate.setId(UUID.randomUUID().toString());
        roleToUpdate.setName("test.role");
        roleToUpdate.setComposite(true);
        RoleRepresentation.Composites composites = new RoleRepresentation.Composites();
        composites.setRealm(Collections.singleton("master"));
        Map<String, List<String>> clients = new HashMap<>();
        clients.put("testClient", Collections.singletonList("testClient"));
        composites.setClient(clients);
        roleToUpdate.setComposites(composites);

        doReturn(responseEntity).when(restTemplate).exchange(eq(ROLES + "test.role"), eq(HttpMethod.PUT), any(), eq(Response.class));
        doReturn(roleRepresentationsResponse).when(restTemplate).getForEntity(eq(COMPOSITES), any());
        doReturn(new RoleRepresentation[0]).when(roleRepresentationsResponse).getBody();
        doReturn(responseEntity).when(restTemplate).exchange(eq(ROLES + "test.role"), eq(HttpMethod.DELETE), any(), eq(Response.class));
        doReturn(responseEntity).when(restTemplate).postForEntity(eq(COMPOSITES), any(), eq(Response.class));

        roleService.updateRole(roleToUpdate);

        Mockito.verify(restTemplate).exchange(
                Mockito.eq(ROLES + "test.role"),
                Mockito.eq(HttpMethod.PUT), Mockito.any(HttpEntity.class), Mockito.eq(Response.class));

        Mockito.verify(restTemplate).postForEntity(eq(COMPOSITES), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity httpEntity = httpEntityCaptor.getValue();
        Set<KeycloakRestRoleService.IdObject> body = (Set<KeycloakRestRoleService.IdObject>) httpEntity.getBody();
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("master")));
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("testClient")));
    }
}
