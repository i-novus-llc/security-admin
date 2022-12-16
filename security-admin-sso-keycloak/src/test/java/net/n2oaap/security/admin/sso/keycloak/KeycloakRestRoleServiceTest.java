package net.n2oaap.security.admin.sso.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestApplication.class,
        properties = {"spring.liquibase.enabled=false",
                "audit.service.url=Mocked", "audit.client.enabled=false"})
@Import(TestConfig.class)
public class KeycloakRestRoleServiceTest {

    public static final String ROLES = "/auth/admin/realms/master/roles/";
    public static final String COMPOSITES = "/auth/admin/realms/master/roles/test.role/composites";

    @Autowired
    private KeycloakRestRoleService roleService;
    @Autowired
    private AdminSsoKeycloakProperties properties;
    @Autowired
    public ObjectMapper objectMapper;
    public static MockWebServer mockBackEnd;

    @BeforeEach
    public void before() {
        properties.setServerUrl(String.format("http://127.0.0.1:%s/auth", mockBackEnd.getPort()));
    }

    @BeforeAll
    static void setUp() throws IOException {
        mockBackEnd = new MockWebServer();
        mockBackEnd.start();
    }

    @AfterAll
    static void tearDown() throws IOException {
        mockBackEnd.shutdown();
    }

    @Test
    public void testCreateCompositesRole() throws IOException, InterruptedException {
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse());
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setId("testId");
        mockBackEnd.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(roleRepresentation)).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));

        RoleRepresentation role = new RoleRepresentation();
        role.setName("test.role");
        role.setComposite(true);
        role.setDescription("test composite role");
        RoleRepresentation.Composites composites = new RoleRepresentation.Composites();
        composites.setRealm(Collections.singleton("master"));
        Map<String, List<String>> clients = new HashMap<>();
        clients.put("testClient", Collections.singletonList("testClient"));
        composites.setClient(clients);
        role.setComposites(composites);

        roleService.createRole(role);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(ROLES, recordedRequest.getPath());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(COMPOSITES, recordedRequest.getPath());

        Set<KeycloakRestRoleService.IdObject> body = objectMapper.readValue(recordedRequest.getBody().readUtf8(), new TypeReference<Set<KeycloakRestRoleService.IdObject>>() {
        });
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("master")));
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("testClient")));
    }

    @Test
    public void testUpdateRoleRemoveCompositeRole() throws JsonProcessingException, InterruptedException {
        mockBackEnd.enqueue(new MockResponse());
        RoleRepresentation[] roleRepresentations = new RoleRepresentation[1];
        RoleRepresentation role = new RoleRepresentation();
        role.setId(UUID.randomUUID().toString());
        role.setName("test.role");
        role.setComposite(true);
        roleRepresentations[0] = role;
        mockBackEnd.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(roleRepresentations)).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        mockBackEnd.enqueue(new MockResponse());

        roleService.updateRole(role);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(ROLES + "test.role", recordedRequest.getPath());

        mockBackEnd.takeRequest();

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals(COMPOSITES, recordedRequest.getPath());

        Set<KeycloakRestRoleService.IdObject> body = objectMapper.readValue(recordedRequest.getBody().readUtf8(), new TypeReference<Set<KeycloakRestRoleService.IdObject>>() {
        });
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals(role.getId())));
    }

    @Test
    public void testUpdateRoleAddCompositeRole() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(new RoleRepresentation[0])).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        mockBackEnd.enqueue(new MockResponse());

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

        roleService.updateRole(roleToUpdate);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(ROLES + "test.role", recordedRequest.getPath());

        mockBackEnd.takeRequest();

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(COMPOSITES, recordedRequest.getPath());

        Set<KeycloakRestRoleService.IdObject> body = objectMapper.readValue(recordedRequest.getBody().readUtf8(), new TypeReference<Set<KeycloakRestRoleService.IdObject>>() {
        });
        assertNotNull(body);
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("master")));
        assertTrue(body.stream().anyMatch(ob -> ob.getId().equals("testClient")));
    }
}
