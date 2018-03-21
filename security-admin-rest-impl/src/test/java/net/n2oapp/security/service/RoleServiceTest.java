package net.n2oapp.security.service;

/**
 * Created by otihonova on 09.11.2017.
 */


import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

/**
 * Тест сервиса управления ролями пользователя
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class RoleServiceTest {


    @Autowired
    private RoleService service;


    @Test
    public void testUp() throws Exception {
        assertNotNull(service);
    }


}
