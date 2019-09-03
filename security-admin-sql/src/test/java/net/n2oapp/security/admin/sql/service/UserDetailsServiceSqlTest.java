package net.n2oapp.security.admin.sql.service;

import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.model.UserDetailsToken;
import net.n2oapp.security.admin.api.service.UserDetailsService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Arrays;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNotNull;

/**
 * Тест SQL реализации сервиса предоставления ролей/пермишенов по пользователю
 */
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, properties = "")
@TestPropertySource("classpath:test.properties")
@AutoConfigureTestDatabase
public class UserDetailsServiceSqlTest {

    @Autowired
    private UserDetailsService service;

    @Test
    public void testUp() {
        assertNotNull(service);
    }

    @Test
    public void loadUserDetailsTest() {
        UserDetailsToken u = new UserDetailsToken();
        u.setUsername("test");
        u.setEmail("test@example.com");
        u.setSurname("surname1");
        u.setName("name1");
        u.setRoleNames(Arrays.asList("user", "admin"));

        User user = service.loadUserDetails(u);
        assertThat(user.getUsername(), is(u.getUsername()));
        assertThat(user.getEmail(), is(u.getEmail()));
        assertThat(user.getExtUid(), is(u.getExtUid()));
        assertThat(user.getSurname(), is(u.getSurname()));
        assertThat(user.getName(), is(u.getName()));

        assertThat(user.getPatronymic(), is("patronymic1"));
        assertThat(user.getPassword(), is("password1"));
        assertThat(user.getIsActive(), is(true));
        assertThat(user.getFio(), is("surname1 name1 patronymic1"));
        assertThat(user.getRoles().size(), is(2));


        assertThat(user.getRoles().get(0).getId(), is(1));
        assertThat(user.getRoles().get(0).getName(), is("user"));
        assertThat(user.getRoles().get(0).getCode(), is("code1"));
        assertThat(user.getRoles().get(0).getDescription(), is("description1"));

        assertThat(user.getRoles().get(0).getPermissions().get(0).getCode(), is("test"));
        assertThat(user.getRoles().get(0).getPermissions().get(0).getName(), is("test"));
        assertThat(user.getRoles().get(0).getPermissions().get(0).getParentCode(), nullValue());
        assertThat(user.getRoles().get(0).getPermissions().get(0).getHasChildren(), nullValue());

        assertThat(user.getRoles().get(0).getPermissions().get(1).getCode(), is("test2"));
        assertThat(user.getRoles().get(0).getPermissions().get(1).getName(), is("test2"));
        assertThat(user.getRoles().get(0).getPermissions().get(1).getParentCode(), is("test"));
        assertThat(user.getRoles().get(0).getPermissions().get(0).getHasChildren(), nullValue());


        assertThat(user.getRoles().get(1).getId(), is(2));
        assertThat(user.getRoles().get(1).getName(), is("admin"));
        assertThat(user.getRoles().get(1).getCode(), is("code2"));
        assertThat(user.getRoles().get(1).getDescription(), is("description2"));
        assertThat(user.getRoles().get(1).getPermissions().size(), is(0));
    }

    /**
     * Пользователя не существует в бд
     */
    @Test
    public void loadUserDetailsTestWithoutUser() {
        UserDetailsToken u = new UserDetailsToken();
        u.setUsername("test5");
        u.setEmail("test@example.com");
        u.setSurname("surname1");
        u.setName("name1");
        u.setExtUid("0c161cec-fbc7-42e4-b0d5-5224f7e5751a");
        u.setRoleNames(Arrays.asList("user", "admin"));

        User user = service.loadUserDetails(u);

        assertThat(user.getUsername(), is(u.getUsername()));
        assertThat(user.getRoles().size(), is(2));
        assertThat(user.getRoles().get(0).getId(), is(1));
        assertThat(user.getRoles().get(0).getName(), is("user"));
        assertThat(user.getRoles().get(0).getCode(), is("code1"));
        assertThat(user.getRoles().get(0).getDescription(), is("description1"));

        assertThat(user.getRoles().get(0).getPermissions().size(), is(2));
        assertThat(user.getRoles().get(0).getPermissions().get(0).getName(), is("test"));
        assertThat(user.getRoles().get(0).getPermissions().get(0).getCode(), is("test"));
        assertThat(user.getRoles().get(0).getPermissions().get(1).getName(), is("test2"));
        assertThat(user.getRoles().get(0).getPermissions().get(1).getCode(), is("test2"));
        assertThat(user.getRoles().get(0).getPermissions().get(1).getParentCode(), is("test"));

        assertThat(user.getRoles().get(1).getId(), is(2));
        assertThat(user.getRoles().get(1).getName(), is("admin"));
        assertThat(user.getRoles().get(1).getCode(), is("code2"));
        assertThat(user.getRoles().get(1).getDescription(), is("description2"));

    }
}
