package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.model.UserRegisterForm;
import net.n2oapp.security.admin.base.UserRoleServiceTestBase;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

/**
 * Тест сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class UserServiceImplTest extends UserRoleServiceTestBase {

    @Captor
    private ArgumentCaptor<MimeMessage> mimeMessageArgumentCaptor;

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    public void testUp() {
        assertNotNull(userService);
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
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = userService.register(user);
        assertEquals("testUser", result.getUsername());
        assertEquals("test@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertNull(result.getExpirationDate());
        assertTrue(result.getIsActive());
        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
            Object content = mimeMessageArgumentCaptor.getValue().getContent();
            assertTrue(content.toString().contains("<p>Уважаемый <span>surname</span> <span>name</span>!</p>"));
            assertTrue(content.toString().contains("<p>Вы зарегистрированы в системе.</p>"));
            assertTrue(content.toString().contains("<p>Логин для входа: <span>testUser</span></p>"));
        } catch (IOException | MessagingException e) {
            fail();
        }
        userService.delete(result.getId());
    }

    @Test
    public void testTemporaryUserRegistration() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testTemporaryUser");
        user.setEmail("testTemporary@test.ru");
        user.setPassword("1234ABCabc,");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        user.setExpirationDate(LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22));

        User result = userService.register(user);

        UserEntity userEntity = userRepository.findById(result.getId()).get();

        assertEquals(user.getExpirationDate(), userEntity.getExpirationDate());

        assertEquals("testTemporaryUser", result.getUsername());
        assertEquals("testTemporary@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(user.getExpirationDate(), result.getExpirationDate());
        assertTrue(result.getIsActive());
        userService.delete(result.getId());
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
        User result = userService.register(user);

        try {
            Mockito.verify(emailSender, Mockito.timeout(10000).atLeastOnce()).send(mimeMessageArgumentCaptor.capture());
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

    @Test
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

    @Test
    public void checkValidations() {
        User user = userService.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        checkValidationUsername(user);
        userService.delete(user.getId());
        userService.setEmailAsUsername(Boolean.TRUE);
        user = userService.create(newUser());
        checkValidationEmail(user);
        checkValidationPassword(user);
        userService.delete(user.getId());
        UserForm userForm = newUser();
        userForm.setSnils("112-233-445 95");
        userForm.setEmail(null);
        userService.setEmailAsUsername(Boolean.FALSE);
        user = userService.create(userForm);
        checkValidationEmail(user);
        checkValidationPassword(user);
        userService.delete(user.getId());
    }

    @Test
    public void selfDelete() {
        UserForm user = newUser();
        user.setUsername("SelfDelete");
        user.setEmail("SelfDelete@gmail.com");
        User result = userService.create(user);

        Throwable thrown = catchThrowable(() -> {
            userService.delete(result.getId());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.selfDelete", thrown.getMessage());

        SecurityContextHolder.getContext().setAuthentication(null);
        userService.delete(result.getId());
    }

    @Test
    public void testCheckUniqueUsername() {
        assertFalse(userService.checkUniqueUsername("test2"));
        assertTrue(userService.checkUniqueUsername("nonExistentUser"));
    }

    /**
     * Проверка, что при некорректно введеном уровне пользователя в критерии, не последует ошибки
     * и при этом возвращаемый список пользователей будет пуст
     */
    @Test
    public void findAllUsersWithBadUserLevelTest() {
        UserCriteria criteria = new UserCriteria();
        criteria.setUserLevel("wrong");
        assertTrue(userService.findAll(criteria).isEmpty());
    }

    @Test
    public void findAllUsersByLastActionDate() {
        UserCriteria criteria = new UserCriteria();
        criteria.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
        assertThat(userService.findAll(criteria).getTotalElements()).isZero();
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser2");
        user.setEmail("test2@test.ru");
        user.setPassword("1234ABCabc,");
        User result = userService.register(user);
        assertThat(userService.findAll(criteria).getTotalElements()).isEqualTo(1);
        userService.delete(result.getId());
    }

    @Test
    public void testFindAllByCriteria() {
        // empty criteria
        UserCriteria criteria = new UserCriteria();
        List<User> users = userService.findAll(criteria).getContent();
        assertEquals(2, users.size());

        // find by username
        criteria.setUsername("test");
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(1, (int) users.get(0).getId());
        assertEquals("test", users.get(0).getUsername());

        // find by email
        criteria.setUsername(null);
        criteria.setEmail("example2");
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(2, (int) users.get(0).getId());
        assertEquals("test@example2.com", users.get(0).getEmail());

        // find by fio
        criteria.setEmail(null);
        criteria.setFio("surname2");
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(2, (int) users.get(0).getId());
        assertEquals("surname2", users.get(0).getSurname());

        // find by isActive
        criteria.setFio(null);
        criteria.setIsActive("yes");
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(1, (int) users.get(0).getId());
        assertEquals(true, users.get(0).getIsActive());

        // find by regionId
        criteria.setIsActive(null);
        criteria.setRegionId(2);
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(2, (int) users.get(0).getId());

        // find by departmentId
        criteria.setRegionId(null);
        criteria.setDepartmentId(1);
        users = userService.findAll(criteria).getContent();
        assertEquals(1, users.size());
        assertEquals(1, (int) users.get(0).getId());

        // find by organizationId
        // find by roleIds
        // find by system
    }

    @Test
    public void testPatch() {
        checkUpdateUser();
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

        User result = userService.register(user);
        User simpleDetailsUser = userService.loadSimpleDetails(result.getId());

        assertEquals(result.getId(), simpleDetailsUser.getId());
        assertEquals(result.getUsername(), simpleDetailsUser.getUsername());
        assertEquals(result.getEmail(), simpleDetailsUser.getEmail());
        assertNotNull(simpleDetailsUser.getTemporaryPassword());

        userService.delete(result.getId());
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
}