package net.n2oaap.security.admin.sso.keycloak;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import okhttp3.mockwebserver.RecordedRequest;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestApplication.class,
        properties = {"spring.liquibase.enabled=false"})
@Import(TestConfig.class)
public class KeycloakSsoUserRoleProviderTest {

    private static final String USERS = "/auth/admin/realms/master/users/";
    private static final String ROLES = "/auth/admin/realms/master/roles/";
    public static final String ROLE_MAPPINGS_REALM = "/role-mappings/realm";
    private String externalUuid;

    @Autowired
    private KeycloakSsoUserRoleProvider provider;

    @Autowired
    private AdminSsoKeycloakProperties properties;

    @Autowired
    public ObjectMapper objectMapper;

    public static MockWebServer mockBackEnd;

    private SsoUser ssoUser;

    @BeforeEach
    public void before() {
        ssoUser = new SsoUser();
        ssoUser.setEmail("email");
        ssoUser.setUsername("username");
        ssoUser.setSurname("surname");
        ssoUser.setName("name");
        ssoUser.setIsActive(true);
        ssoUser.setPassword("123");

        externalUuid = UUID.randomUUID().toString();
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
    public void testCreateUser() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse().addHeader(HttpHeaders.LOCATION, URI.create(externalUuid)));
        mockBackEnd.enqueue(new MockResponse().setBody(provider.objectMapper.writeValueAsString(roleRepresentation()[0])).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        mockBackEnd.enqueue(new MockResponse());

        List<String> requiredActions = new ArrayList<>();
        requiredActions.add("requiredAction");
        ssoUser.setRequiredActions(requiredActions);

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(99);
        role.setCode("test.role");
        roles.add(role);
        ssoUser.setRoles(roles);

        SsoUser user = provider.createUser(ssoUser);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(USERS, recordedRequest.getPath());

        String body = recordedRequest.getBody().readUtf8();
        UserRepresentation capturedUserRepresentation = objectMapper.readValue(body, UserRepresentation.class);
        assertNotNull(capturedUserRepresentation);
        assertEquals(ssoUser.getUsername(), capturedUserRepresentation.getUsername());
        assertEquals(ssoUser.getName(), capturedUserRepresentation.getFirstName());
        assertEquals(ssoUser.getSurname(), capturedUserRepresentation.getLastName());
        assertEquals(ssoUser.getEmail(), capturedUserRepresentation.getEmail());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals(ROLES + role.getCode(), recordedRequest.getPath());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid + ROLE_MAPPINGS_REALM, recordedRequest.getPath());

        body = recordedRequest.getBody().readUtf8();
        List<RoleRepresentation> capturedRoleRepresentations = objectMapper.readValue(body, new TypeReference<List<RoleRepresentation>>() {
        });
        assertNotNull(capturedRoleRepresentations);
        assertEquals("test.role", capturedRoleRepresentations.get(0).getName());
        assertEquals(externalUuid, user.getExtUid());
        assertEquals("KEYCLOAK", user.getExtSys());
        assertEquals(ssoUser.getUsername(), user.getUsername());
        assertEquals(ssoUser.getSurname(), user.getSurname());
        assertEquals(ssoUser.getName(), user.getName());
    }

    @Test
    public void testUpdateUserRemoveRolesUpdatePassword() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse().setBody(provider.objectMapper.writeValueAsString(roleRepresentation())).setHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse());
        ssoUser.setExtUid(externalUuid);
        provider.updateUser(ssoUser);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid, recordedRequest.getPath());

        String body = recordedRequest.getBody().readUtf8();
        UserRepresentation userRepresentation = objectMapper.readValue(body, UserRepresentation.class);
        assertNotNull(userRepresentation);
        assertEquals(ssoUser.getUsername(), userRepresentation.getUsername());
        assertEquals(ssoUser.getName(), userRepresentation.getFirstName());
        assertEquals(ssoUser.getSurname(), userRepresentation.getLastName());
        assertEquals(ssoUser.getEmail(), userRepresentation.getEmail());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid + ROLE_MAPPINGS_REALM, recordedRequest.getPath());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid + ROLE_MAPPINGS_REALM, recordedRequest.getPath());

        body = recordedRequest.getBody().readUtf8();
        List<RoleRepresentation> capturedRoleRepresentations = objectMapper.readValue(body, new TypeReference<List<RoleRepresentation>>() {
        });
        assertNotNull(capturedRoleRepresentations);
        assertEquals("test.role", capturedRoleRepresentations.get(0).getName());


        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid + "/reset-password", recordedRequest.getPath());

        body = recordedRequest.getBody().readUtf8();
        CredentialRepresentation credentialRepresentation = objectMapper.readValue(body, CredentialRepresentation.class);
        assertNotNull(credentialRepresentation);
        assertEquals("password", credentialRepresentation.getType());
        assertEquals("123", credentialRepresentation.getValue());
        assertFalse(credentialRepresentation.isTemporary());
    }

    @Test
    public void testUpdateUserAddRoles() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse());
        mockBackEnd.enqueue(new MockResponse().setBody(objectMapper.writeValueAsString(roleRepresentation()[0])).addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
        mockBackEnd.enqueue(new MockResponse());
        ssoUser.setExtUid(externalUuid);
        ssoUser.setPassword(null);

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(99);
        role.setCode("test");
        roles.add(role);
        ssoUser.setRoles(roles);
        provider.updateUser(ssoUser);

        mockBackEnd.takeRequest();
        mockBackEnd.takeRequest();
        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("GET", recordedRequest.getMethod());
        assertEquals(ROLES + "test", recordedRequest.getPath());

        recordedRequest = mockBackEnd.takeRequest();
        assertEquals("POST", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid + ROLE_MAPPINGS_REALM, recordedRequest.getPath());

        String body = recordedRequest.getBody().readUtf8();
        List<RoleRepresentation> capturedRoleRepresentations = objectMapper.readValue(body, new TypeReference<List<RoleRepresentation>>() {
        });
        assertNotNull(capturedRoleRepresentations);
        assertEquals("test", capturedRoleRepresentations.get(0).getName());
    }

    @Test
    public void testDeleteUsers() throws InterruptedException {
        mockBackEnd.enqueue(new MockResponse());
        ssoUser.setExtUid(externalUuid);
        provider.deleteUser(ssoUser);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("DELETE", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid, recordedRequest.getPath());
    }

    @Test
    public void testChangeActive() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse());
        ssoUser.setExtUid(externalUuid);
        ssoUser.setUsername(null);
        ssoUser.setSurname(null);
        ssoUser.setName(null);
        ssoUser.setEmail(null);
        ssoUser.setIsActive(false);
        provider.changeActivity(ssoUser);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid, recordedRequest.getPath());

        String body = recordedRequest.getBody().readUtf8();
        UserRepresentation capturedUserRepresentation = objectMapper.readValue(body, UserRepresentation.class);
        assertNotNull(capturedUserRepresentation);
        assertFalse(capturedUserRepresentation.isEnabled());
    }

    @Test
    public void testResetPassword() throws InterruptedException, JsonProcessingException {
        mockBackEnd.enqueue(new MockResponse());
        ssoUser.setExtUid(externalUuid);
        List<String> requiredActions = new ArrayList<>();
        requiredActions.add("resetPassword");
        ssoUser.setRequiredActions(requiredActions);

        provider.resetPassword(ssoUser);

        RecordedRequest recordedRequest = mockBackEnd.takeRequest();
        assertEquals("PUT", recordedRequest.getMethod());
        assertEquals(USERS + externalUuid, recordedRequest.getPath());

        String body = recordedRequest.getBody().readUtf8();
        UserRepresentation capturedUserRepresentation = objectMapper.readValue(body, UserRepresentation.class);
        assertNotNull(capturedUserRepresentation);
        assertEquals("resetPassword", capturedUserRepresentation.getRequiredActions().get(0));
    }

    private RoleRepresentation[] roleRepresentation() {
        RoleRepresentation[] roleRepresentations = new RoleRepresentation[1];
        RoleRepresentation role = new RoleRepresentation();
        role.setId(UUID.randomUUID().toString());
        role.setName("test.role");
        role.setComposite(true);
        roleRepresentations[0] = role;
        return roleRepresentations;
    }
}
