package net.n2oapp.security.admin.base;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.ws.rs.NotFoundException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.assertEquals;

public abstract class UserRoleServiceTestBase {

    @Autowired
    protected UserServiceImpl userService;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected RoleService roleService;

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @MockBean
    protected JavaMailSender emailSender;


    protected void checkUpdateUser() {
        UserForm userForm = new UserForm();
        userForm.setUsername("username");
        userForm.setEmail("username@username.username");
        userForm.setPassword("1234ABCabc,");
        userForm.setPasswordCheck(userForm.getPassword());
        userForm.setName("name");
        userForm.setSurname("surname");
        userForm.setPatronymic("patronymic");
        userForm.setSnils("124-985-753 00");

        User result = userService.create(userForm);

        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());

        Map<String, Object> userInfo = new HashMap<>();

        Map<String, Object> finalUserInfo = userInfo;
        Throwable thrown = catchThrowable(() -> {
            userService.patch(finalUserInfo);
        });
        assertEquals("exception.userWithoutIdAndUsername", thrown.getMessage());

        userInfo.put("id", result.getId());
        userInfo.put("expirationDate", LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22));

        result = userService.patch(userInfo);

        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());
        assertEquals(userInfo.get("expirationDate"), result.getExpirationDate());

        UserEntity entity = userRepository.findById(result.getId()).get();

        assertEquals(entity.getExpirationDate(), userInfo.get("expirationDate"));

        userInfo.put("username", "supapupausername");
        userInfo.put("surname", "supasurname");

        result = userService.patch(userInfo);

        assertEquals("supapupausername", result.getUsername());
        assertEquals("supasurname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("supasurname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());

        userInfo = new HashMap<>();
        userInfo.put("id", result.getId());
        userInfo.put("username", "username");
        userInfo.put("email", "username@username.username");
        userInfo.put("accountTypeCode", "testAccountTypeCode");
        userInfo.put("password", "1234ABCabc,");
        userInfo.put("passwordCheck", "1234ABCabc,");
        userInfo.put("name", "name");
        userInfo.put("surname", "surname");
        userInfo.put("patronymic", "patronymic");
        userInfo.put("snils", "124-985-753 00");

        result = userService.patch(userInfo);

        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());

        userService.delete(result.getId());

        try {
            userService.patch(Collections.EMPTY_MAP);
            assert false;
        } catch (UserException ex) {
            assertThat(ex.getCode()).isEqualTo("exception.userWithoutIdAndUsername");
        }

        try {
            userService.patch(Map.of("id", Integer.MAX_VALUE));
            assert false;
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("HTTP 404 Not Found");
        }

        try {
            userService.patch(Map.of("username", "nonExistingUser"));
            assert false;
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("HTTP 404 Not Found");
        }

        result = userService.patch(Map.of("username", "test"));
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("test");

        result = userService.patch(Map.of("id", 1));
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("test");

    }

    protected void checkValidationEmail(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setEmail("test.mail.ru");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("test.mail@ru");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setEmail("@mail.ru");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongEmail", thrown.getMessage());
        user.setEmail("UserEmail@gmail.com");
    }

    protected void checkValidationPassword(User user) {

        Throwable thrown = catchThrowable(() -> {
            user.setPassword("pass");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.passwordLength", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("password ");
            user.setPasswordCheck("password ");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongSymbols", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userpassword");
            user.setPasswordCheck("userpassword");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uppercaseLetters", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("USERPASSWORD");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.lowercaseLetters", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.numbers", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword1");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.specialSymbols", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setPassword("userPassword2$");
            user.setPasswordCheck("userPassword1$");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.passwordsMatch", thrown.getMessage());
        user.setPassword("userPassword1$");
    }

    protected void checkValidationUsername(User user) {
        Throwable thrown = catchThrowable(() -> {
            user.setUsername("$pass");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongUsername", thrown.getMessage());
        thrown = catchThrowable(() -> {
            userService.create(newUser());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueUsername", thrown.getMessage());
        thrown = catchThrowable(() -> {
            user.setUsername("test");
            userService.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.uniqueUsername", thrown.getMessage());
        user.setUsername("userName4");
    }

    protected static UserForm newUser() {
        UserForm user = new UserForm();
        user.setUsername("userName2");
        user.setName("user1");
        user.setSurname("userSurname");
        user.setPatronymic("userPatronymic");
        user.setEmail("UserEmail@gmail.com");
        user.setSendOnEmail(true);
        user.setPassword("userPassword1$");
        user.setPasswordCheck("userPassword1$");
        user.setIsActive(true);
        return user;
    }

    protected UserForm form(User user) {
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
        return form;
    }

    protected static RoleForm newRole() {
        RoleForm role = new RoleForm();
        role.setName("test-name");
        role.setCode("test-code");
        role.setDescription("test-desc");
        List<String> permissions = new ArrayList<>();
        permissions.add("test");
        role.setPermissions(permissions);
        return role;
    }

    protected RoleForm form(Role role) {
        RoleForm form = new RoleForm();
        form.setId(role.getId());
        form.setName(role.getName());
        form.setCode(role.getCode());
        form.setDescription(role.getDescription());
        form.setPermissions(role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toList()));
        return form;
    }
}
