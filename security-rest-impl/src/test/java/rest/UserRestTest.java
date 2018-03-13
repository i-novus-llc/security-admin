package rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.rest.api.UserRestService;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import net.n2oapp.security.TestApplication;
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

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
        Set<Integer> roles = new HashSet<Integer>();
        roles.add(role.getId());

        Page<User> user = client.search(1, 4, "test", "name1", "surname1", "patronymic1", true);
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
        user.setName("userName1Update");
        User updateUser = client.update(user);
        assertEquals("userName1Update", updateUser.getName());
        return updateUser;
    }

    private void delete(Integer id) {
        client.delete(id);
        User user = client.getById(id);
        assertNull(user);
    }



    private static User newUser() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setName("userName1");
        user1.setSurname("userSurname");
        user1.setPatronymic("userPatronymic");
        user1.setEmail("UserEmail");
        user1.setPassword("userPassword");
        user1.setIsActive(true);
        List<Integer> roles = new ArrayList<>();
        roles.add(1);
        user1.setRoleIds(roles);
        return user1;
    }
}
