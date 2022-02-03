package net.n2oapp.security.admin.service;

import net.n2oapp.security.admin.api.service.AccountService;
import net.n2oapp.security.admin.api.service.PermissionService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import javax.ws.rs.NotFoundException;

import static org.junit.Assert.assertNotNull;

/**
 * Тест сервиса управления правами доступа
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class AccountServiceImplTest {

    @Autowired
    private AccountService service;

    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }

    /**
     * Проверка, что удаление аккаунта по несуществующему идентификатору приводит к NotFoundException
     */
    @Test(expected = NotFoundException.class)
    public void deleteAccountByNotExistsId() {
        service.delete(-1);
    }

}
