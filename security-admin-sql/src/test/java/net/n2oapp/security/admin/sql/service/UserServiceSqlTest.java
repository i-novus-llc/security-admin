package net.n2oapp.security.admin.sql.service;

import com.icegreen.greenmail.junit.GreenMailRule;
import com.icegreen.greenmail.util.GreenMailUtil;
import com.icegreen.greenmail.util.ServerSetup;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserForm;
import net.n2oapp.security.admin.api.service.UserService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * Тест SQL реализации сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class UserServiceSqlTest {

    @Rule
    public final GreenMailRule greenMail = new GreenMailRule(new ServerSetup(2525, null, "smtp"));

    @Autowired
    private UserService service;

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }


    @Test
    public void crud() {
        User user = create();
        update(form(user));
        delete(user.getId());
    }

    private User create() {
        User user = service.create(newUser(true));
        assertNotNull(service.getById(user.getId()));
        assertTrue(greenMail.waitForIncomingEmail(1000, 1));
        User userWithtMail = service.create(newUser(false));
        assertNotNull(service.getById(userWithtMail.getId()));
        return user;
    }

    private User update(UserForm user) {
        user.setName("userName1Update");
        User updateUser = service.update(user);
        assertEquals("userName1Update", service.getById(user.getId()).getName());
        return updateUser;
    }

    private void delete(Integer id) {
        service.delete(id);
        User user = service.getById(id);
        assertNull(user);
    }

    @Test
    public void search() throws Exception {
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        UserCriteria criteria = new UserCriteria();
        criteria.setPage(1);
        criteria.setSize(4);
        criteria.setUsername("test");
        criteria.setFio("surname1 name1 patronymic1");
        criteria.setIsActive(true);
        criteria.setRoleIds(roles);
        Page<User> user = service.findAll(criteria);
        assertEquals(1, user.getTotalElements());
    }

    private static UserForm newUser(Boolean generate) {
        UserForm user = new UserForm();
        user.setUsername("userName2");
        user.setName("user1");
        user.setSurname("userSurname");
        user.setPatronymic("userPatronymic");
        user.setEmail("test@test.com");
        if (!generate) {
            user.setPassword("userPassword1$");
            user.setPasswordCheck("userPassword1$");
        }
        user.setGuid("1708934f-171e-431e-9fa7-b81fe9ee0b54");
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
