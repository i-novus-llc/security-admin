package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
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
import static org.junit.Assert.*;


/**
 * Тест сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class UserServiceImplTest {

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
        service.delete(user.getId());
    }

    @Test
    public void testCheckUniqueUsername() {
        assertFalse( service.checkUniqueUsername("test2"));
        assertTrue(service.checkUniqueUsername("nonExistentUser"));
    }


    private void checkValidationEmail(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setEmail("test.mail.ru");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@ru");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("@mail.ru");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        user.setEmail("UserEmail@gmail.com");
    }

    private void checkValidationPassword(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setPassword("pass");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.passwordLength", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userpassword");
            user.setPasswordCheck("userpassword");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uppercaseLetters", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("USERPASSWORD");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.lowercaseLetters", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.numbers", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword1");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.specialSymbols", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword2$");
            user.setPasswordCheck("userPassword1$");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.passwordsMatch", thrown.getMessage());
        user.setPassword("userPassword1$");
    }

    private void checkValidationUsername(User user) {
        Throwable thrown = catchThrowable(() -> {
            user.setUsername("$pass");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongUsername", thrown.getMessage());
        thrown = catchThrowable(() -> {
            service.create(newUser());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueUsername", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setUsername("test");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueUsername", thrown.getMessage());
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
        form.setNewPassword(user.getPassword());
        form.setPasswordCheck(user.getPasswordCheck());
        form.setIsActive(true);
        form.setRoles(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return form;
    }


}