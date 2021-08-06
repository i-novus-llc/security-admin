package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestApplication.class,
        properties = {"access.keycloak.serverUrl=http://127.0.0.1:8085/auth", "spring.liquibase.enabled=false",
                "audit.service.url=Mocked", "audit.client.enabled=false"})
@Import(TestConfig.class)
public class KeycloakSsoUserRoleProviderTest {

    private static final String USERS = "http://127.0.0.1:8085/auth/admin/realms/master/users/";
    private static final String ROLES = "http://127.0.0.1:8085/auth/admin/realms/master/roles/";
    public static final String ROLE_MAPPINGS_REALM = "/role-mappings/realm";
    private String externalUuid;

    @MockBean
    private RestTemplate restTemplate;
    @Autowired
    private KeycloakSsoUserRoleProvider provider;

    @Mock
    private ResponseEntity roleRepresentationsResponse;
    @Mock
    private ResponseEntity oneRoleRepresentationResponse;

    private ResponseEntity<ResponseImpl> responseEntity;
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
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setLocation(URI.create(externalUuid));

        responseEntity = new ResponseEntity<>(httpHeaders, HttpStatus.OK);

        RoleRepresentation[] roleRepresentations = new RoleRepresentation[1];
        RoleRepresentation role = new RoleRepresentation();
        role.setId(UUID.randomUUID().toString());
        role.setName("test.role");
        role.setComposite(true);
        roleRepresentations[0] = role;

        mockRestTemplate(roleRepresentations);
    }


    @Test
    public void testCreateUser() {

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

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(USERS), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity captorValue = httpEntityCaptor.getValue();
        UserRepresentation capturedUserRepresentation = (UserRepresentation) captorValue.getBody();

        assertNotNull(capturedUserRepresentation);
        assertEquals(ssoUser.getUsername(), capturedUserRepresentation.getUsername());
        assertEquals(ssoUser.getName(), capturedUserRepresentation.getFirstName());
        assertEquals(ssoUser.getSurname(), capturedUserRepresentation.getLastName());
        assertEquals(ssoUser.getEmail(), capturedUserRepresentation.getEmail());

        verify(restTemplate).getForEntity(eq(ROLES + role.getCode()), any());

        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), httpEntityCaptor.capture(), eq(Response.class));

        captorValue = httpEntityCaptor.getValue();
        List<RoleRepresentation> capturedRoleRepresentations = (List<RoleRepresentation>) captorValue.getBody();

        assertNotNull(capturedRoleRepresentations);
        assertEquals("test.role", capturedRoleRepresentations.get(0).getName());

        assertEquals(externalUuid, user.getExtUid());
        assertEquals("KEYCLOAK", user.getExtSys());
        assertEquals(ssoUser.getUsername(), user.getUsername());
        assertEquals(ssoUser.getSurname(), user.getSurname());
        assertEquals(ssoUser.getName(), user.getName());
    }

    @Test
    public void testUpdateUserRemoveRolesUpdatePassword() {
        ssoUser.setExtUid(externalUuid);

        provider.updateUser(ssoUser);

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity captorValue = httpEntityCaptor.getValue();
        UserRepresentation capturedUserRepresentation = (UserRepresentation) captorValue.getBody();

        assertNotNull(capturedUserRepresentation);
        assertEquals(ssoUser.getUsername(), capturedUserRepresentation.getUsername());
        assertEquals(ssoUser.getName(), capturedUserRepresentation.getFirstName());
        assertEquals(ssoUser.getSurname(), capturedUserRepresentation.getLastName());
        assertEquals(ssoUser.getEmail(), capturedUserRepresentation.getEmail());

        verify(restTemplate).getForEntity(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), any());

        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), eq(HttpMethod.DELETE), httpEntityCaptor.capture(), eq(Response.class));

        captorValue = httpEntityCaptor.getValue();
        List<RoleRepresentation> capturedRoleRepresentations = (List<RoleRepresentation>) captorValue.getBody();

        assertNotNull(capturedRoleRepresentations);
        assertEquals("test.role", capturedRoleRepresentations.get(0).getName());

        httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(USERS + externalUuid + "/reset-password"), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Response.class));

        captorValue = httpEntityCaptor.getValue();
        CredentialRepresentation credentialRepresentation = (CredentialRepresentation) captorValue.getBody();

        assertNotNull(credentialRepresentation);
        assertEquals("password", credentialRepresentation.getType());
        assertEquals("123", credentialRepresentation.getValue());
        assertFalse(credentialRepresentation.isTemporary());
    }

    @Test
    public void testUpdateUserAddRoles() {
        ssoUser.setExtUid(externalUuid);
        ssoUser.setPassword(null);

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(99);
        role.setCode("test");
        roles.add(role);
        ssoUser.setRoles(roles);

        doReturn(oneRoleRepresentationResponse).when(restTemplate).getForEntity(eq(ROLES + "test"), any());

        provider.updateUser(ssoUser);

        verify(restTemplate).getForEntity(eq(ROLES + "test"), any());

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).postForEntity(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity captorValue = httpEntityCaptor.getValue();
        List<RoleRepresentation> capturedRoleRepresentations = (List<RoleRepresentation>) captorValue.getBody();

        assertNotNull(capturedRoleRepresentations);
        assertEquals("test", capturedRoleRepresentations.get(0).getName());

    }

    @Test
    public void testDeleteUsers() {
        ssoUser.setExtUid(externalUuid);
        provider.deleteUser(ssoUser);
        verify(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.DELETE), any(), eq(Response.class));
    }

    @Test
    public void testChangeActive() {
        ssoUser.setExtUid(externalUuid);
        ssoUser.setUsername(null);
        ssoUser.setSurname(null);
        ssoUser.setName(null);
        ssoUser.setEmail(null);
        ssoUser.setIsActive(false);
        provider.changeActivity(ssoUser);

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity captorValue = httpEntityCaptor.getValue();
        UserRepresentation capturedUserRepresentation = (UserRepresentation) captorValue.getBody();

        assertNotNull(capturedUserRepresentation);
        assertFalse(capturedUserRepresentation.isEnabled());
    }

    @Test
    public void testResetPassword() {
        ssoUser.setExtUid(externalUuid);
        List<String> requiredActions = new ArrayList<>();
        requiredActions.add("resetPassword");
        ssoUser.setRequiredActions(requiredActions);

        provider.resetPassword(ssoUser);

        ArgumentCaptor<HttpEntity> httpEntityCaptor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.PUT), httpEntityCaptor.capture(), eq(Response.class));

        HttpEntity captorValue = httpEntityCaptor.getValue();
        UserRepresentation capturedUserRepresentation = (UserRepresentation) captorValue.getBody();

        assertNotNull(capturedUserRepresentation);
        assertEquals("resetPassword", capturedUserRepresentation.getRequiredActions().get(0));
    }



    private void mockRestTemplate(RoleRepresentation[] roleRepresentations) {
        doReturn(responseEntity).when(restTemplate).postForEntity(eq(USERS), any(), eq(Response.class));
        doReturn(roleRepresentationsResponse).when(restTemplate).getForEntity(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), any());
        doReturn(responseEntity).when(restTemplate).postForEntity(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), any(), eq(Response.class));
        doReturn(responseEntity).when(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.PUT), any(), eq(Response.class));
        doReturn(responseEntity).when(restTemplate).exchange(eq(USERS + externalUuid + "/reset-password"), eq(HttpMethod.PUT), any(), eq(Response.class));
        doReturn(responseEntity).when(restTemplate).exchange(eq(USERS + externalUuid + ROLE_MAPPINGS_REALM), eq(HttpMethod.DELETE), any(), eq(Response.class));
        doReturn(responseEntity).when(restTemplate).exchange(eq(USERS + externalUuid), eq(HttpMethod.DELETE), any(), eq(Response.class));

        doReturn(roleRepresentationsResponse).when(restTemplate).getForEntity(eq(ROLES), any());
        doReturn(oneRoleRepresentationResponse).when(restTemplate).getForEntity(eq(ROLES + roleRepresentations[0].getName()), eq(RoleRepresentation.class));

        doReturn(roleRepresentations).when(roleRepresentationsResponse).getBody();
        doReturn(roleRepresentations[0]).when(oneRoleRepresentationResponse).getBody();

    }
}
