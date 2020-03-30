package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.rest.api.PermissionRestService;
import net.n2oapp.security.admin.rest.api.criteria.RestPermissionCriteria;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
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
public class PermissionRestTest {

    @Autowired
    @Qualifier("permissionRestServiceJaxRsProxyClient")
    private PermissionRestService client;

    @Test
    public void getAllByParentId() throws Exception {
        Page<Permission> permissions = client.getAll("test", new RestPermissionCriteria());
        assertEquals(1, permissions.getTotalElements());
        assertEquals("test2", permissions.getContent().get(0).getCode());
    }

    @Test
    public void getAll() throws Exception {
        Page<Permission> permissions = client.getAll(null, new RestPermissionCriteria());
        assertEquals(2, permissions.getTotalElements());
    }

    /**
     * Проверка, что при некорректно введеном уровне пользователя в критерии, не последует ошибки
     * и при этом возвращаемый список прав доступа будет пуст
     */
    @Test
    public void findAllPermissionsWithBadUserLevelTest() {
        RestPermissionCriteria criteria = new RestPermissionCriteria();
        criteria.setUserLevel("wrong");
        assertEquals(true, client.getAll(null, criteria).isEmpty());
    }

    /**
     * Проверка, что при заданном критерии по уровню пользователя, будет возвращено корректное число прав доступа
     * Также проверяется, что поиск не чувствителен к регистру
     */
    @Test
    public void findAllPermissionsByUserLevel() {
        RestPermissionCriteria criteria = new RestPermissionCriteria();
        criteria.setUserLevel("federal");
        assertEquals(1, client.getAll(null, criteria).getTotalElements());
    }

}
