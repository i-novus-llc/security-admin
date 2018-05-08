package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.*;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Тест SQL реализации сервиса управления ролями
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class RoleServiceSqlTest {

    @Autowired
    private RoleService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }


    @Test
    public void crud() {
        Role role = create();
        update(form(role));
        delete(role.getId());
    }
    private Role create() {
        Role role = service.create(newRole());
        assertNotNull(service.getById(role.getId()));
        return role;
    }

    private Role update(RoleForm role) {
        role.setName("userName1Update");
        Role updateRole = service.update(role);
        assertEquals("userName1Update", service.getById(role.getId()).getName() );
        return updateRole;
    }

    private void delete(Integer id) {
        service.delete(id);
        Role role = service.getById(id);
        assertNull(role);
    }

    @Test
    public void search() throws Exception {
        List<Integer> permissions = new ArrayList<>();
        permissions.add(1);
        RoleCriteria criteria = new RoleCriteria();
        criteria.setPage(0);
        criteria.setSize(4);
        criteria.setName("user");
        criteria.setDescription("description1");
        criteria.setPermissionIds(permissions);
        Page<Role> role = service.findAll(criteria);
        assertEquals(1, role.getTotalElements());
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
