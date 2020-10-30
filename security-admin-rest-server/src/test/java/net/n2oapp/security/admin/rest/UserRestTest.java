package net.n2oapp.security.admin.rest;

import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetup;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.rest.api.UserDetailsRestService;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Тест Rest сервиса управления пользователями
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class UserRestTest {

    @RegisterExtension
    public final GreenMailExtension greenMail = new GreenMailExtension(new ServerSetup(2525, null, "smtp"));

    @Autowired
    @Qualifier("userRestServiceJaxRsProxyClient")
    private UserRestService client;

    @Autowired
    @Qualifier("userDetailsRestServiceJaxRsProxyClient")
    private UserDetailsRestService userDetailsRestService;


    @Test
    public void search() {
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        RestUserCriteria criteria = new RestUserCriteria();
        criteria.setPage(0);
        criteria.setSize(4);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "fio"));
        criteria.setOrders(orders);
        Page<User> user = client.findAll(criteria);
        assertThat(user.getContent().stream().map(User::getSurname).collect(Collectors.toList()), hasItem("surname3"));
        assertThat(user.getContent().stream().map(User::getSurname).collect(Collectors.toList()), hasItem("surname1"));
        criteria.setUsername("test");
        criteria.setFio(" surname1 name1  patronymic1");
        criteria.setIsActive("yes");
        criteria.setRoleIds(roles);
        user = client.findAll(criteria);
        assertEquals(1, user.getTotalElements());

    }

    @Test
    public void searchByRoleCodes() {

        List<String> roleCodes = new ArrayList<>();
        roleCodes.add("code1");
        roleCodes.add("code2");

        RestUserCriteria criteria = new RestUserCriteria();
        criteria.setPage(0);
        criteria.setSize(4);

        criteria.setRoleCodes(roleCodes);
        Page<User> userPage = client.findAll(criteria);
        assertEquals(1, userPage.getTotalElements());

        List<User> content = userPage.getContent();
        assertEquals(content.size(), 1);

        User user = content.get(0);

        assertEquals(user.getId().intValue(), 1);
        assertEquals(user.getUsername(), "test");
        assertEquals(user.getFio(), "surname1 name1 patronymic1");
        assertEquals(user.getEmail(), "test@example.com");
        assertEquals(user.getSurname(), "surname1");
        assertEquals(user.getName(), "name1");
        assertEquals(user.getPatronymic(), "patronymic1");
        assertNull(user.getPassword());
        assertEquals(user.getPasswordHash(), "password1");
        assertNull(user.getPasswordCheck());
        assertNull(user.getTemporaryPassword());
        assertEquals(user.getIsActive(), Boolean.TRUE);

        Role role1 = new Role();
        role1.setId(1);
        role1.setName("user");
        role1.setCode("code1");
        role1.setDescription("description1");
        role1.setNameWithSystem("user");

        Role role2 = new Role();
        role2.setId(2);
        role2.setName("admin");
        role2.setCode("code2");
        role2.setDescription("description2");
        role2.setNameWithSystem("admin");

        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName().equals(role1.getName())));
        assertTrue(user.getRoles().stream().anyMatch(r -> r.getName().equals(role2.getName())));

        assertNull(user.getSnils());
        assertNull(user.getUserLevel());
        assertNull(user.getDepartment());
        assertNull(user.getRegion());
        assertNull(user.getOrganization());
        assertNull(user.getStatus());
        assertNull(user.getClientId());
    }

    @Test
    public void crud() {
        greenMail.setUser("inovus.sec@gmail.com", "");
        User user = create();
        update(form(user));
        delete(user.getId());
        user = register();
        delete(user.getId());
    }

    @Test
    public void testUserDetails() {
        UserDetailsToken userDetailsToken = new UserDetailsToken();
        userDetailsToken.setUsername("test");
        userDetailsToken.setRoleNames(Arrays.asList("code1", "code2"));
        userDetailsToken.setEmail("test@email.ru");
        userDetailsToken.setExtUid(UUID.randomUUID().toString());
        userDetailsToken.setName("name");
        userDetailsToken.setSurname("surname");

        RestUserDetailsToken token = new RestUserDetailsToken(userDetailsToken);

        User user = userDetailsRestService.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getName().equals("name");
        assert user.getSurname().equals("surname");
        assert user.getRoles().size() == 2;
        assert user.getRoles().get(0).getPermissions().size() == 2;
        // проверяем удаление роли
        token = new RestUserDetailsToken();
        token.setUsername("test");
        token.setRoleNames(Arrays.asList("code1"));
        user = userDetailsRestService.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getRoles().size() == 1;
        assert user.getRoles().get(0).getPermissions().size() == 2;
        // проверяем добавление новой роли
        token = new RestUserDetailsToken();
        token.setUsername("test");
        token.setRoleNames(Arrays.asList("code1", "code3"));
        user = userDetailsRestService.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getRoles().size() == 2;
        assert user.getRoles().get(0).getPermissions().size() == 2;
    }

    @Test
    public void findAll() {
        RestUserCriteria criteria = new RestUserCriteria();
        criteria.setLastActionDate(LocalDateTime.now(Clock.systemUTC()));

        UserForm userForm = new UserForm();
        userForm.setUsername("username");
        userForm.setEmail("username@username.username");
        userForm.setAccountTypeCode("testAccountType1");
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

        User user = client.create(userForm);

        Sort.Order order = new Sort.Order(Sort.Direction.ASC, "fio");
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(order);
        criteria.setSize(10);
        criteria.setPage(0);
        criteria.setOrders(orders);
        criteria.setUsername(user.getUsername());
        criteria.setFio("surname name patronymic");
        criteria.setEmail(user.getEmail());
        criteria.setIsActive("yes");
        criteria.setRoleCodes(roleCodes);
        criteria.setRoleIds(roleIds);
        criteria.setDepartmentId(1);
        criteria.setRegionId(1);
        criteria.setOrganizations(orgIds);

        Page<User> users = client.findAll(criteria);

        assertEquals(1, users.getContent().size());
        assertEquals("username", users.getContent().get(0).getUsername());
        assertEquals("surname name patronymic", users.getContent().get(0).getFio());
        assertEquals("username@username.username", users.getContent().get(0).getEmail());
        assertEquals("surname", users.getContent().get(0).getSurname());
        assertEquals("name", users.getContent().get(0).getName());
        assertEquals("patronymic", users.getContent().get(0).getPatronymic());
        assertEquals("124-985-753 00", users.getContent().get(0).getSnils());
        assertEquals(UserLevel.NOT_SET, users.getContent().get(0).getUserLevel());
        assertEquals(UserStatus.REGISTERED, users.getContent().get(0).getStatus());
        Assertions.assertThat(users.getContent().get(0).getDepartment().getId()).isEqualTo(1);
        Assertions.assertThat(users.getContent().get(0).getRegion().getId()).isEqualTo(1);
        Assertions.assertThat(users.getContent().get(0).getOrganization().getId()).isEqualTo(1);
        assertTrue(users.getContent().get(0).getIsActive());
        client.delete(user.getId());
    }

    @Test
    public void testGeneratePassword() {
        Password password = client.generatePassword();
        assertThat(password.password, instanceOf(String.class));
        assertThat(password.password.length(), is(16));
    }

    private User create() {
        User user = client.create(newUser());
        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertEquals((Integer) 1, user.getRoles().iterator().next().getId());
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        return user;
    }

    private User register() {
        UserRegisterForm user = new UserRegisterForm();
        user.setUsername("testRegisterUsername");
        user.setEmail("testRegisterEmail@mail.ru");
        user.setName("testRegisterName");
        user.setSurname("testRegisterSurname");
        user.setPatronymic("testRegisterPatronymic");
        User result = client.register(user);
        assertNotNull(result);
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        assertThat(result.getUsername(), is("testRegisterUsername"));
        assertThat(result.getEmail(), is("testRegisterEmail@mail.ru"));
        assertThat(result.getName(), is("testRegisterName"));
        assertThat(result.getSurname(), is("testRegisterSurname"));
        assertThat(result.getPatronymic(), is("testRegisterPatronymic"));
        return result;
    }

    private User update(UserForm user) {
        user.setUsername("userName1Update");
        User updateUser = client.update(user);
        assertEquals("userName1Update", updateUser.getUsername());
        return updateUser;
    }

    private void delete(Integer id) {
        client.delete(id);
        User user = client.getById(id);
        assertNull(user);
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
        form.setIsActive(true);
        form.setRoles(user.getRoles().stream().map(Role::getId).collect(Collectors.toList()));
        return form;
    }
}
