package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class RoleRestTest {
    @Autowired
    @Qualifier("roleRestProxyClient")
    private RoleRestService client;

    @Test
    public void search() throws Exception {
        List<Integer> permissions = new ArrayList<>();
        permissions.add(1);
        RestRoleCriteria criteria = new RestRoleCriteria();
        criteria.setPage(1);
        criteria.setSize(4);
        criteria.setName("user");
        criteria.setDescription("description1");
        criteria.setPermissionIds(permissions);
        Page<Role> role = client.findAll(criteria);
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
