package net.n2oapp.security.admin.service;

import net.n2oapp.platform.i18n.UserException;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.RoleForm;
import net.n2oapp.security.admin.api.service.PermissionService;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.Assert.*;

/**
 * Тест сервиса управления правами доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class PermissionServiceImplTest {

    @Autowired
    private PermissionService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
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

}
