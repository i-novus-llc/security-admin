package net.n2oaap.security.admin.sso.keycloak;

import net.n2oapp.security.admin.sso.keycloak.KeycloakRestUserService;
import net.n2oapp.security.admin.sso.keycloak.SsoKeycloakConfiguration;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Этот тест предназначен только для локального использования, в рамках разработки. Он реально создает,
 * изменяет и удаляет пользователя в keycloak
 * Для запуска в properties в keycloak.adminClientSecret указать секрет своего клиента access-admin
 * в keycloak.serverUrl указать адрес своего keycloak
 * и в строке role1.setId("a73ffd30-13af-4918-83c9-93000c8c8590"); указать id существующей роли
 */
@Ignore
@RunWith(SpringRunner.class)
@SpringBootConfiguration
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = SsoKeycloakConfiguration.class, properties = {
        "access.keycloak.serverUrl=http://127.0.0.1:8085/auth", "access.keycloak.adminClientSecret=177b14bc-ad98-46bc-ac32-2c0f424a8a52"
})
public class KeycloakRestUserServiceTest {

    @Autowired
    private KeycloakRestUserService userService;

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
        role1.setId("a73ffd30-13af-4918-83c9-93000c8c8590");
        role1.setName("sec.admin");
        userRoles.add(role1);
        userService.addUserRoles(userGuid, userRoles);
        List<RoleRepresentation> actualUserRoles = userService.getActualUserRoles(userGuid);
        assertEquals(actualUserRoles.size(), 3);

        //delete roles
        userService.deleteUserRoles(userGuid, userRoles);
        actualUserRoles = userService.getActualUserRoles(userGuid);
        assertEquals(actualUserRoles.size(), 2);

        //change password, проверяем что просто не падают ошибки
        userService.changePassword(userGuid, "newpass");

        //delete
        userService.deleteUser(userGuid);
        assertNull(userService.getById(userGuid));
    }
}
