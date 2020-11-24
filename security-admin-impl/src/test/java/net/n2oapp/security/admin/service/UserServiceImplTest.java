package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.NotFoundException;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.*;
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
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JavaMailSender emailSender;

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @BeforeClass
    public static void setUp() {
        org.springframework.security.core.userdetails.User contextUser = new org.springframework.security.core.userdetails.User("SelfDelete", "pass", new ArrayList<>());
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(contextUser, new Object());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void testRegistration() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser");
        user.setEmail("test@test.ru");
        user.setPassword("1234ABCabc,");
        user.setAccountTypeCode("testAccountTypeCode");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = service.register(user);
        assertEquals(1, result.getRoles().size());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertTrue(result.getIsActive());

        try {
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Вы зарегистрированы в системе.</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>testUser</span></p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
    }

    @Test
    public void testChangeActive() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser28");
        user.setEmail("test2@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = service.register(user);

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
        User changedUser = service.changeActive(userId);

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

        service.delete(userId);
    }

    @Test
    public void testResetPassword() {
        UserForm userForm = new UserForm();
        service.resetPassword(userForm);

        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser29");
        user.setEmail("test242@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = service.register(user);
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

        service.resetPassword(userForm);

        resetPasswordMailCheck();

        User changedUser = service.getById(userId);

        assertEquals("testUser29", changedUser.getUsername());
        assertEquals("test242@test.ru", changedUser.getEmail());
        assertEquals("name", changedUser.getName());
        assertEquals("surname", changedUser.getSurname());
        assertEquals("patronymic", changedUser.getPatronymic());
        assertTrue(passwordEncoder.matches("Zz123456789!", changedUser.getPasswordHash()));

        userForm = new UserForm();
        userForm.setEmail("test242@test.ru");

        service.resetPassword(userForm);
        resetPasswordMailCheck();

        userForm = new UserForm();
        userForm.setUsername("testUser29");

        service.resetPassword(userForm);
        resetPasswordMailCheck();

        userForm = new UserForm();
        userForm.setSnils("123");
        service.resetPassword(userForm);

        service.delete(userId);
    }

    private void resetPasswordMailCheck() {
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

    @Test
    public void checkValidations() {
        User user = service.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
        service.delete(user.getId());
        service.setEmailAsUsername(Boolean.TRUE);
        user = service.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        service.delete(user.getId());
        UserForm userForm = newUser();
        userForm.setSnils("112-233-445 95");
        userForm.setEmail(null);
        service.setEmailAsUsername(Boolean.FALSE);
        user = service.create(userForm);
        checkValidationEmail(user);
        checkValidationPassword(user);

    }

    @Test
    public void selfDelete() {
        UserForm user = newUser();
        user.setUsername("SelfDelete");
        user.setEmail("SelfDelete@gmail.com");

        Throwable thrown = catchThrowable(() -> {
            service.delete(service.create(user).getId());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.selfDelete", thrown.getMessage());
    }

    @Test
    public void testCheckUniqueUsername() {
        assertFalse(service.checkUniqueUsername("test2"));
        assertTrue(service.checkUniqueUsername("nonExistentUser"));
    }

    /**
     * Проверка, что при некорректно введеном уровне пользователя в критерии, не последует ошибки
     * и при этом возвращаемый список пользователей будет пуст
     */
    @Test
    public void findAllUsersWithBadUserLevelTest() {
        UserCriteria criteria = new UserCriteria();
        criteria.setUserLevel("wrong");
        assertTrue(service.findAll(criteria).isEmpty());
    }

    /**
     * Проверка, что при заданном критерии по уровню пользователя, будет возвращено корректное число пользователей
     * Также проверяется, что поиск не чувствителен к регистру
     */
    @Test
    public void findAllUsersByUserLevel() {
        UserCriteria criteria = new UserCriteria();
        criteria.setUserLevel("federal");
        assertThat(service.findAll(criteria).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllUsersByLastActionDate() {
        UserCriteria criteria = new UserCriteria();
        criteria.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
        assertThat(service.findAll(criteria).getTotalElements()).isEqualTo(0);
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser2");
        user.setEmail("test2@test.ru");
        user.setPassword("1234ABCabc,");
        user.setAccountTypeCode("testAccountTypeCode");
        service.register(user);
        assertThat(service.findAll(criteria).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void testFindAllByCriteria() {
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
        userForm.setIsActive(true);
        List<Integer> roleIds = new ArrayList<>();
        roleIds.add(1);
        userForm.setRoles(roleIds);
        List<String> roleCodes = new ArrayList<>();
        roleCodes.add("code1");
        List<Integer> orgIds = new ArrayList<>();
        orgIds.add(1);

        User user = service.create(userForm);

        UserCriteria criteria = new UserCriteria();
        criteria.setUsername("username");
        criteria.setEmail("username@username.username");
        criteria.setFio("surname name patronymic");
        criteria.setIsActive("yes");
        criteria.setRoleIds(roleIds);
        criteria.setRoleCodes(roleCodes);
        criteria.setRegionId(1);
        criteria.setDepartmentId(1);
        criteria.setOrganizations(orgIds);

        Page<User> users = service.findAll(criteria);

        assertEquals(1, users.getContent().size());
        assertEquals("username", users.getContent().get(0).getUsername());
        assertEquals("surname name patronymic", users.getContent().get(0).getFio());
        assertEquals("username@username.username", users.getContent().get(0).getEmail());
        assertEquals("surname", users.getContent().get(0).getSurname());
        assertEquals("name", users.getContent().get(0).getName());
        assertEquals("patronymic", users.getContent().get(0).getPatronymic());
        assertEquals("124-985-753 00", users.getContent().get(0).getSnils());
        assertEquals(UserLevel.PERSONAL, users.getContent().get(0).getUserLevel());
        assertEquals(UserStatus.REGISTERED, users.getContent().get(0).getStatus());
        assertThat(users.getContent().get(0).getDepartment().getId()).isEqualTo(1);
        assertThat(users.getContent().get(0).getRegion().getId()).isEqualTo(1);
        assertThat(users.getContent().get(0).getOrganization().getId()).isEqualTo(1);
                assertTrue(users.getContent().get(0).getIsActive());
        service.delete(user.getId());
    }

    @Test
    public void testPatch() {
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

        User result = service.create(userForm);

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
            service.patch(finalUserInfo);
        });
        assertEquals("exception.userWithoutIdAndUsername", thrown.getMessage());

        userInfo.put("id", result.getId());

        result = service.patch(userInfo);

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

        userInfo.put("username", "supapupausername");
        userInfo.put("surname", "supasurname");

        result = service.patch(userInfo);

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

        result = service.patch(userInfo);

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

        service.delete(result.getId());

        try {
            service.patch(Collections.EMPTY_MAP);
            assert false;
        } catch (UserException ex) {
            assertThat(ex.getCode()).isEqualTo("exception.userWithoutIdAndUsername");
        }

        try {
            service.patch(Map.of("id", Integer.MAX_VALUE));
            assert false;
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("HTTP 404 Not Found");
        }

        try {
            service.patch(Map.of("username", "nonExistingUser"));
            assert false;
        } catch (NotFoundException ex) {
            assertThat(ex.getMessage()).isEqualTo("HTTP 404 Not Found");
        }

        result = service.patch(Map.of("username", "test"));
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("test");

        result = service.patch(Map.of("id", 1));
        assertThat(result.getId()).isEqualTo(1);
        assertThat(result.getUsername()).isEqualTo("test");
    }

    @Test
    public void testLoadSimpleDetails() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("simpleDetails");
        user.setEmail("simpleDetails@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);

        User result = service.register(user);
        User simpleDetailsUser = service.loadSimpleDetails(result.getId());

        assertEquals(result.getId(), simpleDetailsUser.getId());
        assertEquals(result.getUsername(), simpleDetailsUser.getUsername());
        assertEquals(result.getEmail(), simpleDetailsUser.getEmail());
        assertNotNull(simpleDetailsUser.getTemporaryPassword());

        service.delete(result.getId());
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
            user.setPassword("password ");
            user.setPasswordCheck("password ");
            service.update(form(user));
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.wrongSymbols", thrown.getMessage());
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