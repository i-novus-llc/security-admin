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
    private UserService service;
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



    public User newUser() {
        User user1 = new User();
        user1.setUsername("user1");
        user1.setName("userName1");
        user1.setSurname("userSurname");
        user1.setPatronymic("userPatronymic");
        user1.setEmail("UserEmail");
        user1.setPassword("userPassword");
        user1.setIsActive(true);
        Role role = new Role();
        role.setId(1);
        Set<Integer> roles = new HashSet<Integer>();
        roles.add(role.getId());
      //  user1.setRoleIds(roles);

        return user1;
    }

    @Test
    public void crud() {
        User user= create();
        update(user);
        delete(user.getId());
    }


    public User create() {

        User user = service.create(newUser());
        assertNotNull(user);
        assertEquals(1, user.getRoleIds().size());
        assertEquals((Integer) 1, user.getRoleIds().iterator().next());
        return user;


    }

    public User update(User user) {
        user.setName("userName1Update");
        User updateUser= service.update(user);
        assertEquals("userName1Update", updateUser.getName());
        return updateUser;

    }

    public void delete(Integer id) {
        service.delete(id);
        User user = service.getById(id);
        assertNull(user);
    }

    @Test
    public void search() throws Exception {


        Role role = new Role();
        role.setId(1);
        Set<Integer> roles = new HashSet<Integer>();
        roles.add(role.getId());

        ///UserCriteria userCriteria = new UserCriteria(2, 4);

        Page<User> user = client.search(2,4,"user2","userName2","usersurName2","userPatronymic2",true);
        assertEquals(4, user.getTotalElements());
//        userCriteria.setIsActive(true);
//        user = client.search(userCriteria);
//        assertEquals(3, user.getTotalElements());
//        userCriteria.setUsername("user2");
//        user = client.search(userCriteria);
//        assertEquals(2, user.getTotalElements());
//        userCriteria.setName("userName2");
//        user = client.search(userCriteria);
//        assertEquals(2, user.getTotalElements());
//        userCriteria.setRoleIds(roles);
//        user = client.search(userCriteria);
//        assertEquals(2, user.getTotalElements());
//        userCriteria.setSurname("userSurname2");
//        user = client.search(userCriteria);
//        assertEquals(1, user.getTotalElements());
//        userCriteria.setPatronymic("userPatronymic2");
//        user = client.search(userCriteria);
//        assertEquals(1, user.getTotalElements());


    }


}
