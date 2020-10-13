package net.n2oaap.security.admin.sso.keycloak;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.SsoUser;
import net.n2oapp.security.admin.sso.keycloak.AdminSsoKeycloakProperties;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.apache.cxf.jaxrs.impl.ResponseImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE,
        classes = TestApplication.class,
        properties = {"access.keycloak.serverUrl=http://127.0.0.1:8590/auth", "spring.liquibase.enabled=false",
                "audit.service.url=Mocked", "audit.client.enabled=false"})
public class KeycloakSsoUserRoleProviderTest {

    @Spy
    private AdminSsoKeycloakProperties properties;

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private KeycloakRestRoleService roleService;
    @Mock
    private KeycloakRestUserService userService;

    @Spy
    private SchedulerFactoryBean schedulerFactoryBean;

    @Spy
    private ObjectMapper objectMapper;

    @InjectMocks
    private KeycloakSsoUserRoleProvider provider = new KeycloakSsoUserRoleProvider(properties);
    private SsoUser ssoUser;

    @Captor
    ArgumentCaptor<List<RoleRepresentation>> roleCaptor;

    @Before
    public void before() {
        roleService = new KeycloakRestRoleService(properties, restTemplate);
        userService = new KeycloakRestUserService(properties, restTemplate, roleService);

        provider.setRoleService(roleService);
        provider.setUserService(userService);

        ssoUser = new SsoUser();
        ssoUser.setExtUid(UUID.randomUUID().toString());
        ssoUser.setEmail("email");
        ssoUser.setUsername("username");
        ssoUser.setSurname("surname");
        ssoUser.setName("name");
        ssoUser.setIsActive(true);
        ssoUser.setPassword("123");
    }


    @Test
    public void createUser() {
        ArgumentCaptor<String> uuidCaptor = ArgumentCaptor.forClass(String.class);
        
        String userUuid = UUID.randomUUID().toString();

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create(userUuid));
        ResponseEntity<ResponseImpl> response = new ResponseEntity<>(headers, HttpStatus.OK);

        RoleRepresentation[] roleRepresentationArr = new RoleRepresentation[1];
        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("test");
        roleRepresentation.setId("id");
        roleRepresentationArr[0] = roleRepresentation;

        ResponseEntity<RoleRepresentation[]> roleRepresentationResponse = new ResponseEntity<>(roleRepresentationArr, headers, HttpStatus.OK);

        doReturn(response).when(restTemplate).postForEntity(anyString(), any(), eq(Response.class));
        doReturn(roleRepresentationResponse).when(restTemplate).getForEntity(anyString(), eq(RoleRepresentation[].class));

        List<String> requiredActions = new ArrayList<>();
        requiredActions.add("requiredAction");
        ssoUser.setRequiredActions(requiredActions);

        List<Role> roles = new ArrayList<>();
        Role role = new Role();
        role.setId(99);
        role.setCode("test");
        roles.add(role);
        ssoUser.setRoles(roles);

        SsoUser user = provider.createUser(ssoUser);

        assertEquals(ssoUser.getExtUid(), user.getExtUid());
        assertEquals("KEYCLOAK", user.getExtSys());
        assertEquals(ssoUser.getUsername(), user.getUsername());
        assertEquals(ssoUser.getSurname(), user.getSurname());
        assertEquals(ssoUser.getName(), user.getName());
    }
}