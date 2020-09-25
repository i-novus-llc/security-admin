package net.n2oapp.security.auth;

import net.n2oapp.security.auth.common.User;
import net.n2oapp.security.auth.common.UserParamsUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;

@RunWith(SpringRunner.class)
public class UserParamsUtilTest {
    @Test
    @WithMockUser("test")
    public void getUsernameSuccess() {
        assertThat(UserParamsUtil.getUsername(), is("test"));
    }

    @Test
    public void getUsernameFailure() {
        assertThat(UserParamsUtil.getUsername(), is(""));
    }

    @Test
    public void getSessionIdFailure() {
        assertThat(UserParamsUtil.getSessionId(), is(""));
    }

    @Test
    @WithMockUser(username = "test", roles = {"role1", "role2"})
    public void getUserDetailsAsMap() {
        assertThat(UserParamsUtil.getUserDetailsAsMap().get("username"), is("test"));
        assertThat(UserParamsUtil.getUserDetailsAsMap().get("authorities"), instanceOf(Set.class));
        User user = new User("test");
        user.setSurname("Testov");
        assertThat(UserParamsUtil.getUserDetailsAsMap(user).get("surname"), is("Testov"));
        assertThat(((ArrayList<String>) UserParamsUtil.getUserDetailsAsMap(user).get("roles")).get(0), is("ROLE_USER"));
    }

    @Test
    @WithMockUser("test")
    public void getUserDetailsProperty() {
        assertThat(UserParamsUtil.getUserDetailsProperty("username"), is("test"));
        User user = new User("test");
        user.setSurname("Testov");
        assertThat(UserParamsUtil.getUserDetailsProperty(user, "surname"), is("Testov"));
    }
}
