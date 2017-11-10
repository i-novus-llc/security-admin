package service;

/**
 * Created by otihonova on 09.11.2017.
 */


import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.criteria.RoleCriteria;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.service.RoleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.junit.Assert;

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
    public void serch() throws Exception {
        RoleCriteria roleCriteria =new RoleCriteria(1,2);

        Page<Role> role = service.findAll(roleCriteria);
        Assert.assertNotNull(role);
    }
}
