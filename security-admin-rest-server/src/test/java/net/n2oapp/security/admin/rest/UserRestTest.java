package net.n2oapp.security.admin.rest;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.ServerSetup;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.rest.api.UserRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestUserCriteria;
import net.n2oapp.security.admin.rest.api.criteria.RestUserDetailsToken;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Тест Rest сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class UserRestTest {

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(new ServerSetup(2525, null, "smtp"));

    @Autowired
    @Qualifier("userRestServiceJaxRsProxyClient")
    private UserRestService client;

    @Test
    public void search() throws Exception {
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        RestUserCriteria criteria = new RestUserCriteria();
        criteria.setPage(0);
        criteria.setSize(4);
        List<Sort.Order> orders = new ArrayList<>();
        orders.add(new Sort.Order(Sort.Direction.DESC,"fio"));
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
    public void crud() {
        User user = create();
        update(form(user));
        delete(user.getId());
    }

    @Test
    public void testUserDetails() throws Exception {
        RestUserDetailsToken token = new RestUserDetailsToken();
        token.setUsername("test");
        token.setRoleNames(Arrays.asList("code1", "code2"));
        User user = client.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getRoles().size() == 2;
        assert user.getRoles().get(0).getPermissions().size() == 2;
        // проверяем удаление роли
        token = new RestUserDetailsToken();
        token.setUsername("test");
        token.setRoleNames(Arrays.asList("code1"));
        user = client.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getRoles().size() == 1;
        assert user.getRoles().get(0).getPermissions().size() == 2;
        // проверяем добавление новой роли
        token = new RestUserDetailsToken();
        token.setUsername("test");
        token.setRoleNames(Arrays.asList("code1", "code3"));
        user = client.loadDetails(token);
        assert user.getUsername().equals("test");
        assert user.getRoles().size() == 2;
        assert user.getRoles().get(0).getPermissions().size() == 2;
    }

    private User create() {
        User user = client.create(newUser());
        assertNotNull(user);
        assertEquals(1, user.getRoles().size());
        assertEquals((Integer) 1, user.getRoles().iterator().next().getId());
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        return user;
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
