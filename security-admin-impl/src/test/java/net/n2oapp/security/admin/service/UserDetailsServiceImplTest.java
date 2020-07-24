package net.n2oapp.security.admin.service;

import com.google.common.collect.Lists;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.impl.service.UserDetailsServiceImpl;
import net.n2oapp.security.admin.impl.service.UserServiceImpl;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;

/**
 * Тест сервиса получения данных о пользователе
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@TestPropertySource("classpath:test.properties")
public class UserDetailsServiceImplTest {

    @Autowired
    private UserDetailsServiceImpl detailsService;

    @Autowired
    private UserServiceImpl userService;

    @Test
    public void loadUserDetailsTest() {
        User userDetails = detailsService.loadUserDetails(userDetailsToken());
        assertEquals("testUserName", userDetails.getUsername());
        assertEquals("testName", userDetails.getName());
        assertEquals("test@test.test", userDetails.getEmail());
        assertEquals("testSurname", userDetails.getSurname());
        assertEquals(2, userDetails.getRoles().size());
        userService.delete(userDetails.getId());
    }

    private UserDetailsToken userDetailsToken() {
        UserDetailsToken token = new UserDetailsToken();
        token.setExtUid("21c4d4ed-add5-4d3d-833b-9a02fe018e77");
        token.setExternalSystem("testSystem");
        token.setUsername("testUserName");
        token.setName("testName");
        token.setSurname("testSurname");
        token.setEmail("test@test.test");
        token.setRoleNames(Lists.newArrayList("code1", "code2"));
        return token;
    }
}
