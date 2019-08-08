package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
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

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
public class KeycloakRestRoleServiceTest {

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
    private KeycloakRestRoleService roleService;

/*
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
*/

    @Test
    public void testCRUDRole(){
        //create
        RoleRepresentation role = new RoleRepresentation();
        String name = "test.role";
        role.setName(name);
        role.setComposite(true);
        role.setDescription("test composite role");
        RoleRepresentation.Composites composites = new RoleRepresentation.Composites();
        HashMap<String, List<String>> clientRoles = new HashMap<>();
        clientRoles.put("realm-management", Arrays.asList("eb2ce3aa-5892-42e9-b70a-a901ff020487","60b4c931-0d7a-4dcb-8033-93cd8bcdd6b2"));
        composites.setClient(clientRoles);
        HashSet<String> realmRoles = new HashSet<>();
        realmRoles.add("1b59b5e4-06c1-4352-bcd7-0097ea066d90");
        composites.setRealm(realmRoles);
        role.setComposites(composites);
        String roleGuid = roleService.createRole(role);
        assertNotNull(roleGuid);

        //update
        RoleRepresentation updatedRole = roleService.getByName(name);
        updatedRole.setDescription("new role desc");
        composites = new RoleRepresentation.Composites();
        clientRoles = new HashMap<>();
        clientRoles.put("realm-management", Arrays.asList("eb2ce3aa-5892-42e9-b70a-a901ff020487"));
        composites.setClient(clientRoles);
        updatedRole.setComposites(composites);
        roleService.updateRole(updatedRole);
        updatedRole = roleService.getByName(name);
        assertEquals(updatedRole.getDescription(), "new role desc");
        RoleRepresentation[] roleComposites = roleService.getRoleComposites(name);
        assertEquals(roleComposites.length, 1);
        assertEquals(roleComposites[0].getId(), "eb2ce3aa-5892-42e9-b70a-a901ff020487");

        //delete
        roleService.deleteRole(name);
        assertNull(roleService.getByName(name));
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
