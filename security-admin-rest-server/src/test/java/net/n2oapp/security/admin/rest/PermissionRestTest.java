package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Тест Rest сервиса управления правами доступа
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class PermissionRestTest {

    @Autowired
    @Qualifier("permissionRestProxyClient")
    private PermissionRestService client;

    @Test
    public void getAllByParentId() throws Exception {
        Page<Permission> permissions = client.getAll(1, false);
        assertEquals(1, permissions.getTotalElements());
        assertEquals((Integer) 2, permissions.getContent().get(0).getId());
    }

    @Test
    public void getAllParentIsNull() throws Exception {
        Page<Permission> permissions = client.getAll(1, true);
        assertEquals(1, permissions.getTotalElements());
        assertEquals((Integer) 1, permissions.getContent().get(0).getId());
    }

    @Test
    public void getAll() throws Exception {
        Page<Permission> permissions = client.getAll(null, null);
        assertEquals(2, permissions.getTotalElements());
    }

}
