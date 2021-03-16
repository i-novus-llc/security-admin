package net.n2oapp.security.admin.base;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

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

    @Captor
    protected ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    protected void checkUpdateUser(){
        UserForm userForm = new UserForm();
        userForm.setUsername("username");
        userForm.setEmail("username@username.username");
        userForm.setAccountTypeCode("testAccountTypeCode");
        userForm.setPassword("1234ABCabc,");
        userForm.setPasswordCheck(userForm.getPassword());
        userForm.setDepartmentId(1);
        userForm.setName("name");
        userForm.setSurname("surname");
        userForm.setPatronymic("patronymic");
        userForm.setRegionId(1);
        userForm.setOrganizationId(1);
        userForm.setSnils("124-985-753 00");

        User result = userService.create(userForm);

        assertEquals(1, result.getRoles().size());
        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertThat(result.getDepartment().getId()).isEqualTo(1);
        assertThat(result.getRegion().getId()).isEqualTo(1);
        assertThat(result.getOrganization().getId()).isEqualTo(1);

        Map<String, Object> userInfo = new HashMap<>();

        Map<String, Object> finalUserInfo = userInfo;
        Throwable thrown = catchThrowable(() -> {
            userService.patch(finalUserInfo);
        });
        assertEquals("exception.userWithoutIdAndUsername", thrown.getMessage());

        userInfo.put("id", result.getId());
        userInfo.put("expirationDate", LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22));

        result = userService.patch(userInfo);

        assertEquals(1, result.getRoles().size());
        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertThat(result.getDepartment().getId()).isEqualTo(1);
        assertThat(result.getRegion().getId()).isEqualTo(1);
        assertEquals(userInfo.get("expirationDate"), result.getExpirationDate());

        UserEntity entity = userRepository.findById(result.getId()).get();

        assertEquals(entity.getExpirationDate(), userInfo.get("expirationDate"));

        userInfo.put("username", "supapupausername");
        userInfo.put("surname", "supasurname");

        result = userService.patch(userInfo);

        assertEquals(1, result.getRoles().size());
        assertEquals("supapupausername", result.getUsername());
        assertEquals("supasurname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("supasurname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertThat(result.getDepartment().getId()).isEqualTo(1);
        assertThat(result.getRegion().getId()).isEqualTo(1);

        userInfo = new HashMap<>();
        userInfo.put("id", result.getId());
        userInfo.put("username", "username");
        userInfo.put("email", "username@username.username");
        userInfo.put("accountTypeCode", "testAccountTypeCode");
        userInfo.put("password", "1234ABCabc,");
        userInfo.put("passwordCheck", "1234ABCabc,");
        userInfo.put("departmentId", 1);
        userInfo.put("name", "name");
        userInfo.put("surname", "surname");
        userInfo.put("patronymic", "patronymic");
        userInfo.put("regionId", 1);
        userInfo.put("snils", "124-985-753 00");
        List<Integer> roles = new ArrayList<>();
        roles.add(100);
        userInfo.put("roles", roles);

        result = userService.patch(userInfo);

        assertEquals(1, result.getRoles().size());
        assertEquals("username", result.getUsername());
        assertEquals("surname name patronymic", result.getFio());
        assertEquals("username@username.username", result.getEmail());
        assertEquals("surname", result.getSurname());
        assertEquals("name", result.getName());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals("124-985-753 00", result.getSnils());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertThat(result.getDepartment().getId()).isEqualTo(1);
        assertThat(result.getRegion().getId()).isEqualTo(1);

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

    protected void checkChangeActive() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser28");
        user.setEmail("test2@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = userService.register(user);

        try {
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>testUser28</span></p>"));
            assertTrue(content.toString().contains("<p>Временный пароль:"));
            assertTrue(content.toString().contains("<p>Пожалуйста, измените пароль при следующем входе.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }

        assertEquals("testUser28", result.getUsername());
        assertEquals("test2@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(true, result.getIsActive());

        Integer userId = result.getId();
        User changedUser = userService.changeActive(userId);

        try {
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span> <span>patronymic</span>!</p>"));
            assertTrue(content.toString().contains("Признак активности Вашей учетной записи изменен на \"<span>Нет</span>\"."));
        } catch (IOException | MessagingException e) {
            fail();
        }

        assertEquals("testUser28", changedUser.getUsername());
        assertEquals("test2@test.ru", changedUser.getEmail());
        assertEquals("name", changedUser.getName());
        assertEquals("surname", changedUser.getSurname());
        assertEquals("patronymic", changedUser.getPatronymic());
        assertEquals(false, changedUser.getIsActive());

        userService.delete(userId);
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
        List<Integer> roles = new ArrayList<>();
        roles.add(100);
        user.setRoles(roles);
        user.setStatus(UserStatus.REGISTERED);
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
        form.setRoles(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
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

    protected void checkResetPassword() {
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

        resetPasswordMailCheck();

        User changedUser = userService.getById(userId);

        assertEquals("testUser29", changedUser.getUsername());
        assertEquals("test242@test.ru", changedUser.getEmail());
        assertEquals("name", changedUser.getName());
        assertEquals("surname", changedUser.getSurname());
        assertEquals("patronymic", changedUser.getPatronymic());
        assertTrue(passwordEncoder.matches("Zz123456789!", changedUser.getPasswordHash()));

        userForm = new UserForm();
        userForm.setEmail("test242@test.ru");

        userService.resetPassword(userForm);
        resetPasswordMailCheck();

        userForm = new UserForm();
        userForm.setUsername("testUser29");

        userService.resetPassword(userForm);
        resetPasswordMailCheck();

        userForm = new UserForm();
        userForm.setSnils("123");
        userService.resetPassword(userForm);

        userService.delete(userId);
    }

    protected void resetPasswordMailCheck() {
        try {
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>testUser29</span>!</p>"));
            assertTrue(content.toString().contains("<p>Ваш пароль был сброшен.</p>"));
            assertTrue(content.toString().contains("<p>Временный пароль:"));
            assertTrue(content.toString().contains("<p>Пожалуйста измените пароль при следующем входе.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }

}
