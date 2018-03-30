package net.n2oapp.security.service;

import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
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
import java.util.stream.Collectors;

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
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@ru");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("@mail.ru");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong email", thrown.getMessage());
        user.setEmail("UserEmail@gmail.com");
    }

    private void checkValidationPassword(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setPassword("pass");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password length", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userpassword");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one uppercase letter", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("USERPASSWORD");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one lowercase letter", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one number", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword1");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("Wrong password format. Password must contain at least one special symbol", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword2$");
            user.setPasswordCheck("userPassword1$");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("The password and confirm password fields do not match.", thrown.getMessage());
        user.setPassword("userPassword1$");
    }

    private void checkValidationUsername(User user) {
        Throwable thrown = catchThrowable(() -> {
            user.setUsername("$pass");
            service.update(form(user));
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
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(IllegalArgumentException.class);
        assertEquals("User with such username already exists", thrown.getMessage());
        user.setUsername("userName4");
    }


    private static UserForm newUser() {
        UserForm user = new UserForm();
        user.setUsername("userName2");
        user.setName("user1");
        user.setSurname("userSurname");
        user.setPatronymic("userPatronymic");
        user.setEmail("UserEmail@gmail.com");
        user.setPassword("userPassword1$");
        user.setPasswordCheck("userPassword1$");
        user.setIsActive(true);
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        user.setRoles(roles);
        return user;
    }

    private UserForm form(User user) {
        UserForm form = new UserForm();
        form.setId(user.getId());
        form.setUsername(user.getUsername());
        form.setName(user.getName());
        form.setSurname(user.getSurname());
        form.setPatronymic(user.getPatronymic());
        form.setEmail(user.getEmail());
        form.setPassword(user.getPassword());
        form.setPasswordCheck(user.getPasswordCheck());
        form.setIsActive(true);
        form.setRoles(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return form;
    }


}