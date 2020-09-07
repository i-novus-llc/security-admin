package net.n2oapp.security.admin.service;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
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
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl service;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(new ServerSetup(2525, null, "smtp"));

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

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        try {
            Object content = receivedMessages[0].getContent();
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
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        try {
            Object content = receivedMessages[0].getContent();
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
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser29");
        user.setEmail("test2@test.ru");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = service.register(user);
        greenMail.reset();
        assertEquals("testUser29", result.getUsername());
        assertEquals("test2@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(true, result.getIsActive());

        Integer userId = result.getId();
        UserForm userForm = new UserForm();
        userForm.setId(userId);
        userForm.setPassword("Zz123456789!");

        service.resetPassword(userForm);

        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();
        assertEquals(1, receivedMessages.length);
        try {
            Object content = receivedMessages[0].getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>testUser29</span>!</p>"));
            assertTrue(content.toString().contains("<p>Ваш пароль был сброшен.</p>"));
            assertTrue(content.toString().contains("<p>Временный пароль:"));
            assertTrue(content.toString().contains("<p>Пожалуйста измените пароль при следующем входе.</p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }

        User changedUser = service.getById(userId);

        assertEquals("testUser29", changedUser.getUsername());
        assertEquals("test2@test.ru", changedUser.getEmail());
        assertEquals("name", changedUser.getName());
        assertEquals("surname", changedUser.getSurname());
        assertEquals("patronymic", changedUser.getPatronymic());
        assertTrue(passwordEncoder.matches("Zz123456789!", changedUser.getPasswordHash()));

        service.delete(userId);
    }

    @Test
    public void checkValidations() {
        User user = service.create(newUser());
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
        service.delete(user.getId());
        service.setEmailAsUsername(Boolean.TRUE);
        user = service.create(newUser());
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        checkValidationEmail(user);
        checkValidationPassword(user);
        service.delete(user.getId());
        UserForm userForm = newUser();
        userForm.setSnils("112-233-445 95");
        userForm.setEmail(null);
        service.setEmailAsUsername(Boolean.FALSE);
        user = service.create(userForm);
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
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