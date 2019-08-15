package net.n2oaap.security.admin.sso.keycloak;

import com.github.tomakehurst.wiremock.junit.WireMockRule;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakProperties;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static javax.ws.rs.core.MediaType.APPLICATION_JSON;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SsoKeycloakConfiguration.class, properties = {
        "keycloak.serverUrl=http://127.0.0.1:8590/auth",
})
public class KeycloakRestRoleServiceTest {

    @Autowired
    private SsoKeycloakProperties properties;

    private KeycloakRestRoleService roleService;

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8590);

    @Before
    public void setUp() throws IOException {
        roleService = new KeycloakRestRoleService(properties);
        roleService.setTemplate(new RestTemplate());
        wireMockRule.stubFor(post(urlPathMatching("/auth/admin/realms/master/roles/"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withHeader("Location", "/1b59b5e4-06c1-4352-bcd7-0097ea066d90")));
        wireMockRule.stubFor(get(urlPathMatching("/auth/admin/realms/master/roles/test.role"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", APPLICATION_JSON)
                        .withBody("{\"id\":\"1b59b5e4-06c1-4352-bcd7-0097ea066d90\", \"name\":\"test.role\", \"description\":\"test composite role\", \"composite\":\"false\"}")));
        wireMockRule.stubFor(delete(urlPathMatching("/auth/admin/realms/master/roles/test.role"))
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
}
