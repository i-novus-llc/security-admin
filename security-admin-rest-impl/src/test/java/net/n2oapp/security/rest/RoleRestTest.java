package net.n2oapp.security.rest;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.rest.api.RoleRestService;
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
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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

    private RoleRestService client;

    @Before
    public void setUp() throws Exception {
        List<Object> providers = new ArrayList<>();
        providers.add(jsonProvider);
        client = JAXRSClientFactory.create("http://localhost:" + port + "/" + cxf,
                RoleRestService.class,
                providers);
    }

    @Test
    public void search() throws Exception {
        List<Integer> permissions = new ArrayList<>();
        permissions.add(1);
        Page<Role> role = client.search(1, 4, "user", "description1", permissions);
        assertEquals(1, role.getTotalElements());
    }

    @Test
    public void crud() {
        Integer id = create();
        update(id);
        delete(id);
    }

    public Integer create() {
        Role role = client.create(newRole());
        assertNotNull(role);
        assertEquals(1, role.getPermissions().size());
        assertEquals((Integer) 1, role.getPermissions().iterator().next().getId());
        return role.getId();
    }

    public void update(Integer id) {
        Role role = client.getById(id);
        role.setName("userUpdate");
        client.update(form(role));
        assertEquals("userUpdate", client.getById(id).getName());

    }

    public void delete(Integer id) {
        client.delete(id);
        Role role = client.getById(id);
        assertNull(role);
    }


    private static RoleForm newRole() {
        RoleForm role = new RoleForm();
        role.setName("user1");
        role.setCode("code1");
        role.setDescription("description1");
        List<Integer> permissions = new ArrayList<>();
        permissions.add(1);
        role.setPermissions(permissions);
        return role;
    }

    private RoleForm form(Role role) {
        RoleForm form = new RoleForm();
        form.setId(role.getId());
        form.setName(role.getName());
        form.setCode(role.getCode());
        form.setDescription(role.getDescription());
        form.setPermissions(role.getPermissions().stream().map(Permission::getId).collect(Collectors.toList()));
        return form;
    }


}
