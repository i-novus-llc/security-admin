package net.n2oaap.security.admin.sso.keycloak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;

import net.n2oapp.platform.test.autoconfigure.DefinePort;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakProperties;
import org.apache.cxf.helpers.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "keycloak.serverUrl=http://127.0.0.1:8590/auth"
})
public class KeycloakRestUserServiceTest {

    @Configuration
    @Import(SsoKeycloakConfiguration.class)
    static class Config {
        @Bean
        RestOperations oauth2RestTemplate() {
            RestTemplate restTemplate = new RestTemplate() {
                @Override
                protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                    ClientHttpRequest request = super.createRequest(url, method);
                    request.getHeaders().add("Authorization", "Bearer " + getToken("user1", "user1").getToken());
                    return request;
                }
            };
            return restTemplate;
        }
    }

    @Autowired
    private KeycloakRestUserService userService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8590);

    @Before
    public void setUp() throws IOException {
        wireMockRule.stubFor(post(urlPathMatching("/api/path/v1.0/resourcename"))
                .withRequestBody(containing("\"somethinginheader\":\"50cca0e4-69ea-4247\""))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)));
                        //.withBody(fileToJSON("datafile.with.data.json"))));
    }

    @Test
    public void testCRUDUser() {
        //create
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername("test");
        user.setFirstName("testName");
        user.setLastName("testSurname");
        user.setEmail("test@mail.ru");
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("test");
        user.setCredentials(Arrays.asList(passwordCred));
        String userGuid = userService.createUser(user);
        assertNotNull(userGuid);

        //update
        user.setId(userGuid);
        user.setFirstName("newFirstName");
        user.setLastName("newSurname");
        userService.updateUser(user);
        UserRepresentation updatedUser = userService.getById(userGuid);
        assertEquals(updatedUser.getFirstName(), "newFirstName");
        assertEquals(updatedUser.getLastName(), "newSurname");

        //update roles
        ArrayList<RoleRepresentation> userRoles = new ArrayList<>();
        RoleRepresentation role1 = new RoleRepresentation();
        role1.setId("1b59b5e4-06c1-4352-bcd7-0097ea066d90");
        role1.setName("sec.admin");
        userRoles.add(role1);
        userService.addUserRoles(userGuid, userRoles);
        List<RoleRepresentation> actualUserRoles = userService.getActualUserRoles(userGuid);
        assertEquals(actualUserRoles.size(), 3);

        //delete roles
        userService.deleteUserRoles(userGuid, userRoles);
        actualUserRoles = userService.getActualUserRoles(userGuid);
        assertEquals(actualUserRoles.size(), 2);

        //change password
        userService.changePassword(userGuid, "newpass");
        //todo как проверить что пароль изменился?

        //delete
        userService.deleteUser(userGuid);
        assertNull(userService.getById(userGuid));
    }

    @Test
    public void testExecuteActionsEmail() {
        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername("test2");
        user.setFirstName("testName2");
        user.setLastName("testSurname2");
        user.setEmail("test2@mail.ru");
        CredentialRepresentation passwordCred = new CredentialRepresentation();
        passwordCred.setTemporary(false);
        passwordCred.setType(CredentialRepresentation.PASSWORD);
        passwordCred.setValue("test2");
        user.setCredentials(Arrays.asList(passwordCred));
        String userGuid = userService.createUser(user);
        assertNotNull(userGuid);

        List<String> actions = new ArrayList<>();
        actions.add("VERIFY_EMAIL");
        actions.add("UPDATE_PASSWORD");
        userService.executeActionsEmail(actions, userGuid);
    }


    public static AccessTokenResponse getToken(String username, String password) {
        final String serverUrl = String.format("%s/realms/%s/protocol/openid-connect/token", "http://127.0.0.1:8085/auth", "epmp");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("username", username);
        body.add("password", password);
        body.add("client_id", "security-admin-sso");
        ResponseEntity<AccessTokenResponse> response = new RestTemplate()
                .postForEntity(serverUrl, new HttpEntity<>(body, headers), AccessTokenResponse.class);
        return response.getBody();
    }
}
