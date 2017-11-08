package service;

import net.n2oapp.security.TestApplication;
import net.n2oapp.security.admin.api.criteria.UserCriteria;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.service.UserService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;



/**
 * Тест сервиса фильтрации юзеров
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class  UserServiceTest {



    @Autowired
    private UserService service;

    @Test
    public void search() throws Exception {
        UserCriteria userCriteria =new UserCriteria(1,2);

        Page<User> user = service.findAll(userCriteria);
        Assert.assertNotNull(user);
    }
}