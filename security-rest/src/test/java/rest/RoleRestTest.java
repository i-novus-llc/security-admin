package rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.RoleService;
import net.n2oapp.security.rest.RoleRestService;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import static org.junit.Assert.*;

/**
 * Тест Rest сервиса управления ролями пользователя
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class RoleRestTest {
    @LocalServerPort
    private int port;

    @Value("${cxf.path}")
    private String cxf;
    @Autowired
    private JacksonJsonProvider jsonProvider;
    @Autowired
    private RoleService service;

    private RoleRestService client;

    @Before
    public void setUp() throws Exception {
        List<Object> providers = new ArrayList<>();
        providers.add(jsonProvider);
        client = JAXRSClientFactory.create("http://localhost:" + port + "/" + cxf,
                RoleRestService.class,
                providers);
    }

    public Role newRole(){
        Role role = new Role();
        role.setName("user");
        role.setCode("code1");
        role.setDescription("description1");
        Permission permission = new Permission();
        permission.setId(1);
        Set<Integer> permissions = new HashSet<Integer>();
        permissions.add(permission.getId());
        role.setPermissionIds(permissions);
        return role;

    }

    @Test
    public void crud() {
        Integer id = create();
        update(id);
        delete(id);
    }

    public Integer create() {

        Role role = service.create(newRole());
        assertNotNull(role);
        assertEquals(1, role.getPermissionIds().size());
        assertEquals((Integer) 1, role.getPermissionIds().iterator().next());
        return role.getId();
    }

    public void update(Integer id) {

        Role role = service.getById(id);
        role.setName("userUpdate");
        service.update(role);
        assertEquals("userUpdate", service.getById(id).getName());

    }

    public void delete(Integer id){
        service.delete(id);
        Role role = service.getById(id);
        assertNull(role);


    }


    @Test
    public void search() throws Exception {

        Permission permission = new Permission();
        permission.setId(1);
        Set<Integer> permissions = new HashSet<Integer>();
        permissions.add(permission.getId());

        Page<Role> role =  client.search(2,4,"user","description1");
        assertEquals(2, role.getTotalElements());

//        Page<Role> role = service.findAll(roleCriteria);
//        assertEquals(2, role.getTotalElements());
//
//        roleCriteria.setName("user");
//        role = service.findAll(roleCriteria);
//        assertEquals(1, role.getTotalElements());
//
//        roleCriteria.setDescription("description1");
//        roleCriteria.setPermissionIds(permissions);
//        role = service.findAll(roleCriteria);
//        assertEquals(1, role.getTotalElements());
    }



}
