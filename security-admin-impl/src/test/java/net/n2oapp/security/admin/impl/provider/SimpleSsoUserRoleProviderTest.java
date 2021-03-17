package net.n2oapp.security.admin.impl.provider;

import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.UserRegisterForm;
import net.n2oapp.security.admin.base.UserRoleServiceTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = SimpleSsoUserRoleProviderTest.SecurityAdminContextInitializer.class)
@TestPropertySource("classpath:test.properties")
public class SimpleSsoUserRoleProviderTest extends UserRoleServiceTestBase {

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    static class SecurityAdminContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    "access.keycloak.sync-persistence-enabled=false");
        }
    }

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateUser() {
        User user = userService.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateUser() {
        checkUpdateUser();
    }

    @Test
    @Transactional
    @Rollback
    public void testDeleteUser() {
        UserForm user = newUser();
        Throwable thrown = catchThrowable(() -> {
            userService.delete(userService.create(user).getId());
        });
        assertNull(thrown);
    }

    @Test
    @Transactional
    @Rollback
    public void testChangeActive() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser28");
        user.setEmail("test2@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = userService.register(user);

        assertEquals("testUser28", result.getUsername());
        assertEquals("test2@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(true, result.getIsActive());

        Integer userId = result.getId();
        User changedUser = userService.changeActive(userId);

        assertEquals("testUser28", changedUser.getUsername());
        assertEquals("test2@test.ru", changedUser.getEmail());
        assertEquals("name", changedUser.getName());
        assertEquals("surname", changedUser.getSurname());
        assertEquals("patronymic", changedUser.getPatronymic());
        assertEquals(false, changedUser.getIsActive());

        userService.delete(userId);
    }

    @Test
    @Transactional
    @Rollback
    public void testCreateRole() {
        Role role = roleService.create(newRole());
        assertNotNull(role);
    }

    @Test
    @Transactional
    @Rollback
    public void testUpdateRole() {
        Role role = roleService.create(newRole());
        assertNotNull(role);
        role.setName("demoUser");
        roleService.update(form(role));
    }

    @Test
    public void testDeleteRole() {
        Role role = roleService.create(newRole());
        assertNotNull(role);
        roleService.delete(role.getId());
    }

    @Test
    @Transactional
    @Rollback
    public void testResetPassword() {
        UserForm userForm = new UserForm();
        userService.resetPassword(userForm);

        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser29");
        user.setEmail("test242@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = userService.register(user);
        assertEquals("testUser29", result.getUsername());
        assertEquals("test242@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(true, result.getIsActive());

        Integer userId = result.getId();

        userForm = new UserForm();
        userForm.setId(userId);
        userForm.setPassword("Zz123456789!");
        userForm.setSendOnEmail(true);

        userService.resetPassword(userForm);

        userService.delete(userId);
    }

}
