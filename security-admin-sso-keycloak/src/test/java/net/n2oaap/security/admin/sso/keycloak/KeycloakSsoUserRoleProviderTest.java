package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.sso.keycloak.KeycloakRestRoleService;
import net.n2oapp.security.admin.sso.keycloak.KeycloakSsoUserRoleProvider;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.RoleRepresentation;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {SsoKeycloakTestConfiguration.class}, properties = {"access.keycloak.serverUrl=http://127.0.0.1:8590/auth", "spring.liquibase.enabled=false",
        "audit.service.url=Mocked", "audit.client.enabled=false", "spring.liquibase.enabled=false"})
public class KeycloakSsoUserRoleProviderTest {

    @Autowired
    private KeycloakSsoUserRoleProvider provider;

    @MockBean
    private KeycloakRestRoleService roleService;


    @Test
    public void testGetAll() {

        RoleRepresentation roleRepresentation = new RoleRepresentation();
        roleRepresentation.setName("testRole");
        roleRepresentation.setDescription("testDesc");
        List<RoleRepresentation> roleRepresentationList = new ArrayList<>();
        roleRepresentationList.add(roleRepresentation);

        Mockito.doReturn(roleRepresentationList).when(roleService).getAllRoles();

        List<Role> allRoles = provider.getAllRoles();

        assertEquals(1, allRoles.size());
        assertEquals("testRole", allRoles.get(0).getCode());
        assertEquals("testDesc", allRoles.get(0).getDescription());
    }
}
