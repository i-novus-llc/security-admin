package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.service.PermissionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Тест SQL реализации сервиса управления правами доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class PermissionServiceSqlTest {

    @Autowired
    private PermissionService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }


    @Test
    public void crud() {
        Permission permission = create();
        update(permission);
        delete(permission.getCode());
    }

    private Permission create() {
        Permission permission = service.create(newPermission());
        assertNotNull(service.getByCode(permission.getCode()));
        return permission;
    }

    private Permission update(Permission permission) {
        permission.setName("userName1Update");
        Permission updatePermission = service.update(permission);
        assertEquals("userName1Update", service.getByCode(permission.getCode()).getName());
        return updatePermission;
    }

    private void delete(String code) {
        service.delete(code);
        Permission permission = service.getByCode(code);
        assertNull(permission);
    }

    @Test
    public void getAll() throws Exception {
        List<Permission> permissions = service.getAll();
        assertEquals(2, permissions.size());
    }

    @Test
    public void getAllgetAllByParentId() throws Exception {
        List<Permission> permissions = service.getAllByParentCode("test");
        assertEquals(1, permissions.size());
        assertEquals("test2", permissions.get(0).getCode());
        assertEquals(false, permissions.get(0).getHasChildren());
    }

    @Test
    public void getAllByParentIdIsNull() throws Exception {
        List<Permission> permissions = service.getAllByParentIdIsNull();
        assertEquals(1, permissions.size());
        assertEquals("test", permissions.get(0).getCode());
        assertEquals(true, permissions.get(0).getHasChildren());
    }

    private static Permission newPermission() {
        Permission permission = new Permission();
        permission.setName("user1");
        permission.setCode("code1");
        permission.setParentCode(null);
        return permission;
    }


}
