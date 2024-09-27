package net.n2oapp.security.admin.rest;

import jakarta.ws.rs.NotFoundException;
import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.rest.api.RoleRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestRoleCriteria;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Тест Rest сервиса управления ролями пользователя
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class RoleRestTest {
    @Autowired
    @Qualifier("roleRestServiceJaxRsProxyClient")
    private RoleRestService client;

    @Test
    public void search() throws Exception {
        List<String> permissions = new ArrayList<>();
        permissions.add("test");
        RestRoleCriteria criteria = new RestRoleCriteria();
        criteria.setPage(0);
        criteria.setSize(4);
        criteria.setName("user");
        criteria.setDescription("description1");
        criteria.setPermissionCodes(permissions);
        Page<Role> role = client.findAll(criteria);
        assertEquals(1, role.getTotalElements());
    }

    @Test
    public void crud() {
        Integer id = create();
        update(id);
        delete(id);
    }

    private Integer create() {
        Role role = client.create(newRole());
        assertNotNull(role);
        assertEquals(1, role.getPermissions().size());
        assertEquals("test", role.getPermissions().iterator().next().getCode());
        return role.getId();
    }

    private void update(Integer id) {
        Role role = client.getById(id);
        role.setName("userUpdate");
        client.update(form(role));
        assertEquals("userUpdate", client.getById(id).getName());

    }

    private void delete(Integer id) {
        client.delete(id);
        try {
            client.getById(id);
            fail("Method should throw exception, but he didn't!");
        } catch (NotFoundException ignored) {
        }
    }


    private static RoleForm newRole() {
        RoleForm role = new RoleForm();
        role.setName("test-name");
        role.setCode("test-code");
        role.setDescription("description1");
        List<String> permissions = new ArrayList<>();
        permissions.add("test");
        role.setPermissions(permissions);
        return role;
    }

    private RoleForm form(Role role) {
        RoleForm form = new RoleForm();
        form.setId(role.getId());
        form.setName(role.getName());
        form.setCode(role.getCode());
        form.setDescription(role.getDescription());
        form.setPermissions(role.getPermissions().stream().map(Permission::getCode).collect(Collectors.toList()));
        return form;
    }


}
