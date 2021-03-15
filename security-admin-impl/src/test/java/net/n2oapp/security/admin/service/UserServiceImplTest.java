package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.base.UserRoleServiceTestBase;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.time.Clock;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.*;

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

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Before
    public void before() {
        Mockito.doNothing().when(emailSender).send(mimeMessageArgumentCaptor.capture());
        Mockito.doReturn(new MimeMessage(Session.getDefaultInstance(new Properties()))).when(emailSender).createMimeMessage();
    }

    @Test
    public void testUp() throws Exception {
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
        user.setAccountTypeCode("testAccountTypeCode");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        User result = userService.register(user);
        assertEquals(1, result.getRoles().size());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertEquals("testUser", result.getUsername());
        assertEquals("test@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertNull(result.getExpirationDate());
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
    public void testTemporaryUserRegistration() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testTemporaryUser");
        user.setEmail("testTemporary@test.ru");
        user.setPassword("1234ABCabc,");
        user.setAccountTypeCode("testAccountTypeCode");
        user.setName("name");
        user.setSurname("surname");
        user.setPatronymic("patronymic");
        user.setIsActive(true);
        user.setSendPasswordToEmail(true);
        user.setExpirationDate(LocalDateTime.of(2017, Month.JULY, 9, 11, 6, 22));

        User result = userService.register(user);

        UserEntity userEntity = userRepository.findById(result.getId()).get();

        assertEquals(user.getExpirationDate(), userEntity.getExpirationDate());

        assertEquals(1, result.getRoles().size());
        assertEquals(UserLevel.PERSONAL, result.getUserLevel());
        assertEquals(UserStatus.REGISTERED, result.getStatus());
        assertEquals("testTemporaryUser", result.getUsername());
        assertEquals("testTemporary@test.ru", result.getEmail());
        assertEquals("name", result.getName());
        assertEquals("surname", result.getSurname());
        assertEquals("patronymic", result.getPatronymic());
        assertEquals(user.getExpirationDate(), result.getExpirationDate());
        assertTrue(result.getIsActive());
    }

    @Test
    public void testChangeActive() {
        changeActive();
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

    }

    @Test
    public void selfDelete() {
        UserForm user = newUser();
        user.setUsername("SelfDelete");
        user.setEmail("SelfDelete@gmail.com");

        Throwable thrown = catchThrowable(() -> {
            userService.delete(userService.create(user).getId());
        });
        assertThat(thrown).isInstanceOf(UserException.class);
        assertEquals("exception.selfDelete", thrown.getMessage());
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

    /**
     * Проверка, что при заданном критерии по уровню пользователя, будет возвращено корректное число пользователей
     * Также проверяется, что поиск не чувствителен к регистру
     */
    @Test
    public void findAllUsersByUserLevel() {
        UserCriteria criteria = new UserCriteria();
        criteria.setUserLevel("federal");
        assertThat(userService.findAll(criteria).getTotalElements()).isEqualTo(1);
    }

    @Test
    public void findAllUsersByLastActionDate() {
        UserCriteria criteria = new UserCriteria();
        criteria.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));
        assertThat(userService.findAll(criteria).getTotalElements()).isEqualTo(0);
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testUser2");
        user.setEmail("test2@test.ru");
        user.setPassword("1234ABCabc,");
        user.setAccountTypeCode("testAccountTypeCode");
        userService.register(user);
        assertThat(userService.findAll(criteria).getTotalElements()).isEqualTo(1);
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

        User user = userService.create(userForm);

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

        Page<User> users = userService.findAll(criteria);

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
        userService.delete(user.getId());
    }

    @Test
    public void testPatch() {
        updateUserInfo();
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


}