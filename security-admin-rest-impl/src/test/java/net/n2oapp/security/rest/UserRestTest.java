package net.n2oapp.security.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Тест Rest сервиса управления пользователями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class UserRestTest {

    @LocalServerPort
    private int port;

    @Value("${cxf.path}")
    private String cxf;
    @Autowired
    private JacksonJsonProvider jsonProvider;
    @Autowired
    private UserRestService client;

    @Before
    public void setUp() throws Exception {
        List<Object> providers = new ArrayList<>();
        providers.add(jsonProvider);
        client = JAXRSClientFactory.create("http://localhost:" + port + "/" + cxf,
                UserRestService.class,
                providers);
    }

    @Test
    public void search() throws Exception {
        Role role = new Role();
        role.setId(1);
        List<Integer> roles = new ArrayList<>();
        roles.add(role.getId());
        Page<User> user = client.search(1, 4, "test", "name surname1 patronymic1", true, roles);
        assertEquals(1, user.getTotalElements());
    }

    @Test
    public void crud() {
        User user = create();
        update(user);
        delete(user.getId());
    }

    private User create() {
        User user = client.create(newUser());
        assertNotNull(user);
        assertEquals(1, user.getRoleIds().size());
        assertEquals((Integer) 1, user.getRoleIds().iterator().next());
        return user;
    }

    private User update(User user) {
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


    private static User newUser() {
        User user1 = new User();
        user1.setUsername("userName2");
        user1.setName("user1");
        user1.setSurname("userSurname");
        user1.setPatronymic("userPatronymic");
        user1.setEmail("UserEmail@gmail.com");
        user1.setPassword("userPassword1$");
        user1.setCheckPassword("userPassword1$");
        user1.setIsActive(true);
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        user1.setRoleIds(roles);
        return user1;
    }
}
