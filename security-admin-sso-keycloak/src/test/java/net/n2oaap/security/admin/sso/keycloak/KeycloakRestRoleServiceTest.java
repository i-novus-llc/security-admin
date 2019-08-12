package net.n2oaap.security.admin.sso.keycloak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.AccessTokenResponse;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = {
        "keycloak.serverUrl=http://127.0.0.1:8590/auth"
})
public class KeycloakRestRoleServiceTest {

    @Configuration
    @Import(SsoKeycloakConfiguration.class)
    static class Config {
        @Bean
        RestOperations oauth2RestTemplate() {
            RestTemplate restTemplate = new RestTemplate(); /*{
                @Override
                protected ClientHttpRequest createRequest(URI url, HttpMethod method) throws IOException {
                    ClientHttpRequest request = super.createRequest(url, method);
                    request.getHeaders().add("Authorization", "Bearer " + getToken("user1", "user1").getToken());
                    return request;
                }
            };*/
            return restTemplate;
        }
    }

    @Autowired
    private KeycloakRestRoleService roleService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8590);

    @Before
    public void setUp() throws IOException {

        wireMockRule.stubFor(post(urlPathMatching("/auth/admin/realms/epmp/roles/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withHeader("Location", "/1b59b5e4-06c1-4352-bcd7-0097ea066d90")));
        wireMockRule.stubFor(get(urlPathMatching("/auth/admin/realms/epmp/roles/test.role"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody("{\"id\":\"1b59b5e4-06c1-4352-bcd7-0097ea066d90\", \"name\":\"test.role\", \"description\":\"test composite role\", \"composite\":\"false\"}")));
        wireMockRule.stubFor(delete(urlPathMatching("/auth/admin/realms/epmp/roles/test.role"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)));
    }

    @Test
    public void testCRUDRole(){
        RoleRepresentation role = new RoleRepresentation();
        String name = "test.role";
        role.setName(name);
        role.setComposite(false);
        role.setDescription("test composite role");
        String roleGuid = roleService.createRole(role);
        assertEquals(roleGuid,"1b59b5e4-06c1-4352-bcd7-0097ea066d90");
        RoleRepresentation roleByName = roleService.getByName(name);
        assertEquals(roleByName.getDescription(), "test composite role");
        roleService.deleteRole(name);
    }


    public static AccessTokenResponse getToken(String username, String password) {
        final String serverUrl = String.format("%s/realms/%s/protocol/openid-connect/token", "http://127.0.0.1:8590/auth", "epmp");
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
