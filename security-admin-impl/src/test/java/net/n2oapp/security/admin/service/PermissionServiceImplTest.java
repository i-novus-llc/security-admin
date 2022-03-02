package net.n2oapp.security.admin.service;

import net.n2oapp.platform.test.autoconfigure.EnableEmbeddedPg;
import net.n2oapp.security.admin.api.criteria.PermissionCriteria;
import net.n2oapp.security.admin.api.model.AppSystem;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.PermissionUpdateForm;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.api.service.PermissionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.*;


/**
 * Тест сервиса управления правами доступа
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@EnableEmbeddedPg
public class PermissionServiceImplTest {

    @Autowired
    private PermissionService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    @Test
    public void createTest() {
        Permission permission = service.create(obtainPermission());

        assertEquals("code", permission.getCode());
        assertEquals("name", permission.getName());
        assertEquals(UserLevel.FEDERAL, permission.getUserLevel());
        assertFalse(permission.getUsedInRole());
        assertEquals("system1", permission.getSystem().getCode());

        service.delete(permission.getCode());
    }

    @Test
    public void updateTest() {
        Permission permission = service.create(obtainPermission());

        assertEquals("code", permission.getCode());
        assertEquals("name", permission.getName());
        assertEquals(UserLevel.FEDERAL, permission.getUserLevel());
        assertFalse(permission.getUsedInRole());
        assertEquals("system1", permission.getSystem().getCode());

        PermissionUpdateForm updateForm = new PermissionUpdateForm();
        updateForm.setCode("code");
        updateForm.setName("newName");
        updateForm.setUserLevel(UserLevel.NOT_SET);

        permission = service.update(updateForm);

        assertEquals("code", permission.getCode());
        assertEquals("newName", permission.getName());
        assertEquals(UserLevel.NOT_SET, permission.getUserLevel());
        assertFalse(permission.getUsedInRole());
        assertEquals("system1", permission.getSystem().getCode());
        assertNull(permission.getParent());

        Permission anotherPermission = obtainPermission();
        anotherPermission.setCode("code2");

        service.create(anotherPermission);

        updateForm = new PermissionUpdateForm();
        updateForm.setCode("code2");
        updateForm.setName("newName2");
        updateForm.setUserLevel(UserLevel.NOT_SET);
        updateForm.setParent(permission);

        anotherPermission = service.update(updateForm);

        assertEquals("code2", anotherPermission.getCode());
        assertEquals("newName2", anotherPermission.getName());
        assertEquals(UserLevel.NOT_SET, anotherPermission.getUserLevel());
        assertFalse(anotherPermission.getUsedInRole());
        assertEquals("system1", anotherPermission.getSystem().getCode());
        assertNotNull(anotherPermission.getParent());

        service.delete("code2");
        service.delete("code");
    }

    @Test
    public void getAllTest() {
        PermissionCriteria permissionCriteria = new PermissionCriteria();
        permissionCriteria.setCode("test2");
        permissionCriteria.setName("test2");
        permissionCriteria.setSystemCode("system1");

        Page<Permission> permissions = service.getAll(permissionCriteria);
        assertEquals(1, permissions.getContent().size());
        assertEquals("test2", permissions.getContent().get(0).getCode());
        assertEquals("test2", permissions.getContent().get(0).getName());
        assertEquals("system1", permissions.getContent().get(0).getSystem().getCode());

        permissionCriteria = new PermissionCriteria();
        permissionCriteria.setCode("test");
        permissionCriteria.setWithoutParent(true);

        permissions = service.getAll(permissionCriteria);
        assertEquals(2, permissions.getContent().size());

        permissionCriteria = new PermissionCriteria();
        permissionCriteria.setCode("test");
        permissionCriteria.setName("test");
        permissionCriteria.setWithSystem(true);

        permissions = service.getAll(permissionCriteria);

        assertEquals(3, permissions.getContent().size());
        assertTrue(permissions.getContent().stream().anyMatch(p -> p.getName().equals("system1")));
    }

    @Test
    public void getByCodeTest() {
        Permission result = service.getByCode("test2");

        assertEquals("test2", result.getCode());
        assertEquals("test2", result.getName());
        assertNotNull(result.getParent());
        assertEquals("test", result.getParent().getCode());
        assertNotNull(result.getSystem());
        assertEquals("system1", result.getSystem().getCode());
    }

    @Test
    public void getByCodeSystemTest() {
        Permission result = service.getByCode("$system1");

        assertEquals("$system1", result.getCode());
        assertEquals("system1", result.getName());
        assertNull(result.getParent());
        assertTrue(result.getHasChildren());
    }

    private Permission obtainPermission() {
        Permission permission = new Permission();
        permission.setCode("code");
        permission.setName("name");
        permission.setUserLevel(UserLevel.FEDERAL);
        permission.setHasChildren(false);
        permission.setUsedInRole(false);

        AppSystem system = new AppSystem();
        system.setCode("system1");
        permission.setSystem(system);
        return permission;
    }

}
