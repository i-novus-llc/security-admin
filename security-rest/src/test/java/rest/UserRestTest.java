package rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.admin.api.service.UserService;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import org.apache.cxf.jaxrs.client.JAXRSClientFactory;
import net.n2oapp.security.TestApplication;
import net.n2oapp.security.rest.UserRestService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;


import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by otihonova on 03.11.2017.
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
    private JdbcTemplate jdbcTemplate;


    @Autowired
    private UserRestService client;

    @Before
    public void setUp() throws Exception {
        List<Object> providers = new ArrayList<>();
        providers.add(jsonProvider);
        client = JAXRSClientFactory.create("http://localhost:" + port + "/" + cxf,
                UserRestService.class,
                providers);
        jdbcTemplate.execute("INSERT INTO sec.role(id, name, code) VALUES (1, 'test','test')");

        User user1 = new User();
        user1.setUsername("user2");
        user1.setName("userName2");
        user1.setSurname("userSurname");
        user1.setPatronymic("userPatronymic");
        user1.setEmail("UserEmail");
        user1.setPassword("userPassword");
        user1.setIsActive(true);
        Role role = new Role();
        role.setId(1);
        Set<Integer> roles = new HashSet<Integer>();
        roles.add(role.getId());
        user1.setRoleIds(roles);
        User user2 = new User();
        user2.setUsername("user2");
        user2.setName("userName2");
        user2.setSurname("userSurname2");
        user2.setPatronymic("userPatronymic");
        user2.setEmail("UserEmail");
        user2.setPassword("userPassword");
        user2.setIsActive(true);
        user2.setRoleIds(roles);
        User user3 = new User();
        user3.setUsername("user3");
        user3.setName("userName3");
        user3.setSurname("userSurname3");
        user3.setPatronymic("userPatronymic");
        user3.setEmail("UserEmail");
        user3.setPassword("userPassword");
        user3.setIsActive(true);
        user3.setRoleIds(roles);
        User user4 = new User();
        user4.setUsername("user4");
        user4.setName("userName3");
        user4.setSurname("userSurname3");
        user4.setPatronymic("userPatronymic");
        user4.setEmail("UserEmail");
        user4.setPassword("userPassword");
        user4.setIsActive(true);
        user4.setRoleIds(roles);
        service.create(user1);
        service.create(user2);
        service.create(user3);
        service.create(user4);
    }


    @Test
    public void crudOperations() throws Exception {
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
        user1.setRoleIds(roles);
        Integer id = service.create(user1);
        User user = service.getById(id);
        assertNotNull(user);
        assertEquals(1, user.getRoleIds().size());
        assertEquals((Integer) 1, user.getRoleIds().iterator().next());
        user.setName("userName1Update");
        service.update(user);
        assertEquals("userName1Update", service.getById(id).getName());
        service.delete(id);
        /// assertNull(service.getById(id));
    }

    @Test
    public void test() throws Exception {


        Role role = new Role();
        role.setId(1);
        Set<Integer> roles = new HashSet<Integer>();
        Set<Boolean> active = new HashSet<Boolean>();
        active.add(true);
        active.add(false);



        roles.add(role.getId());
        UserCriteria userCriteria = new UserCriteria(2, 4);

        Page<User> user = service.findAll(userCriteria);
        assertEquals(4, user.getTotalElements());

        userCriteria.setUsername("user2");
        userCriteria.setName("userName2");
        userCriteria.setIsActive(active);
       // userCriteria.setRoleIds(roles);
        user = service.findAll(userCriteria);
        assertEquals(2, user.getTotalElements());

        userCriteria.setSurname("userSurname2");
        userCriteria.setPatronymic("userPatronymic");
        user = service.findAll(userCriteria);
        assertEquals(1, user.getTotalElements());


    }


}
