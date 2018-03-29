package net.n2oapp.security.service;

import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 * Тест сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class UserServiceTest {

    @Autowired
    private UserService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void checkValidations() {
        User user = service.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
    }


    private void checkValidationEmail(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setEmail("test.mail.ru");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@ru");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("@mail.ru");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        user.setEmail("UserEmail@gmail.com");
    }

    private void checkValidationPassword(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setPassword("pass");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password length", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userpassword");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one uppercase letter", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("USERPASSWORD");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one lowercase letter", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one number", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword1");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one special symbol", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword2$");
            user.setCheckPassword("userPassword1$");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("The password and confirm password fields do not match.", thrown.getMessage());
        user.setPassword("userPassword1$");
    }

    private void checkValidationUsername(User user) {
        Throwable thrown = catchThrowable(() -> {
            user.setUsername("$pass");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong username format", thrown.getMessage());
        thrown = catchThrowable(() -> {
            service.create(newUser());
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("User with such username already exists", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setUsername("test");
            service.update(user);
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("User with such username already exists", thrown.getMessage());
        user.setUsername("userName4");
    }


    private static User newUser() {
        User user1 = new User();
        user1.setUsername("userName3");
        user1.setName("user1");
        user1.setSurname("userSurname");
        user1.setPatronymic("userPatronymic");
        user1.setEmail("UserEmail@gmail.com");
        user1.setPassword("userPassword1$");
        user1.setCheckPassword("userPassword1$");
        user1.setIsActive(true);
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        user1.setRoleIds(roles);
        return user1;
    }


}