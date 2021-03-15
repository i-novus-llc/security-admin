package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.base.UserRoleServiceTestBase;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.TestPropertySourceUtils;

import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ContextConfiguration(initializers = SimpleSsoUserRoleProviderTest.SecurityAdminContextInitializer.class)
@TestPropertySource("classpath:test.properties")
public class SimpleSsoUserRoleProviderTest extends UserRoleServiceTestBase {

    static class SecurityAdminContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext applicationContext) {
            TestPropertySourceUtils.addInlinedPropertiesToEnvironment(applicationContext,
                    "access.keycloak.modify-enabled=false");
        }
    }

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    public void testCreateUser() {
        User user = userService.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
    }

    @Test
    public void testUpdateUser() {
        updateUserInfo();
    }

    @Test
    public void testDeleteUser() {
        UserForm user = newUser();
        user.setUsername("SelfDelete");
        user.setEmail("SelfDelete@gmail.com");

        Throwable thrown = catchThrowable(() -> {
            userService.delete(userService.create(user).getId());
        });
        assertNull(thrown);
    }

    @Test
    public void testChangeActive() {
        changeActive();
    }

}
