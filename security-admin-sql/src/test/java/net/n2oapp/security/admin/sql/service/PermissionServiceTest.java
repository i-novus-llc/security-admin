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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Тест сервиса управления правами доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class PermissionServiceTest {

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
        delete(permission.getId());
    }
    private Permission create() {
        Permission permission = service.create(newPermission());
        assertNotNull(service.getById(permission.getId()));
        return permission;
    }

    private Permission update(Permission permission) {
        permission.setName("userName1Update");
        Permission updatePermission = service.update(permission);
        assertEquals("userName1Update", service.getById(permission.getId()).getName() );
        return updatePermission;
    }

    private void delete(Integer id) {
        service.delete(id);
        Permission permission = service.getById(id);
        assertNull(permission);
    }

    @Test
    public void getAll() throws Exception {
        List<Permission> permissions = service.getAll();
        assertEquals(2,permissions.size());
    }

    @Test
    public void getAllgetAllByParentId() throws Exception {
        List<Permission> permissions = service.getAllByParentId(1);
        assertEquals(1,permissions.size());
        assertEquals((Integer)2,permissions.get(0).getId());
    }

    @Test
    public void getAllByParentIdIsNull() throws Exception {
        List<Permission> permissions = service.getAllByParentIdIsNull();
        assertEquals(1,permissions.size());
        assertEquals((Integer)1,permissions.get(0).getId());
    }

    private static Permission newPermission() {
        Permission permission = new Permission();
        permission.setName("user1");
        permission.setCode("code1");
        permission.setParentId(null);
        return permission;
    }


}
