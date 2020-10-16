package net.n2oapp.security.admin.rest;

import net.n2oapp.security.admin.TestApplication;
import net.n2oapp.security.admin.api.model.UserLevel;
import net.n2oapp.security.admin.rest.api.UserLevelRestService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;

import static net.n2oapp.security.admin.api.model.UserLevel.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

/**
 * Тест REST сервиса управления уровнями пользователя
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT,
        properties = "server.port=8290")
@TestPropertySource("classpath:test.properties")
public class UserLevelRestTest {

    @Autowired
    @Qualifier("userLevelRestServiceJaxRsProxyClient")
    private UserLevelRestService client;

    @Test
    public void testGetAll() {
        List<UserLevel> result = client.getAll().getContent();
        List<UserLevel> levels = new ArrayList<>();
        levels.add(ORGANIZATION);
        levels.add(FEDERAL);
        levels.add(REGIONAL);
        assertThat(result.size(), is(3));
        assertThat(levels.contains(result.get(0)), is(true));
        levels.remove(result.get(0));
        assertThat(levels.contains(result.get(1)), is(true));
        levels.remove(result.get(1));
        assertThat(levels.contains(result.get(2)), is(true));
    }


    @Test
    public void testFindAll() {
        List<UserLevel> result = client.getAllForFilter("организац").getContent();

        assertThat(result.size(), is(1));
        assertThat(result.get(0), is(ORGANIZATION));
    }

}
