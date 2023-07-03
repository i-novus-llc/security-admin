package net.n2oapp.security.admin;

import net.n2oapp.platform.test.autoconfigure.pg.EnableEmbeddedPg;
import net.n2oapp.platform.userinfo.UserInfoModel;
import net.n2oapp.security.admin.impl.scheduled.SynchronizationInfo;
import net.n2oapp.security.auth.common.authority.RoleGrantedAuthority;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.test.context.TestPropertySource;
import ru.i_novus.ms.audit.client.UserAccessor;
import ru.i_novus.ms.audit.client.model.User;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

@EnableEmbeddedPg
@SpringBootTest(classes = AdminBackendApplication.class)
@TestPropertySource("classpath:test.properties")
public class BackendApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Autowired
    private UserAccessor userAccessor;

    @Test
    public void contextLoads() {
        assertThat(context, notNullValue());

        Map<String, SynchronizationInfo> beans = context.getBeansOfType(SynchronizationInfo.class);
        assertThat(beans.containsKey("appSysExportJobAndTrigger"), is(true));
        assertThat(beans.containsKey("regionJobAndTrigger"), is(true));
        assertThat(beans.containsKey("departmentJobAndTrigger"), is(true));
        assertThat(beans.containsKey("organizationJobAndTrigger"), is(true));
    }


    @Test
    public void userAccessorTest() {
        String email = "testEmail";
        String username = "TestUsername";

        UserInfoModel userInfoModel = new UserInfoModel(username);
        userInfoModel.email = email;
        SecurityContextHolder.setContext(new SecurityContextImpl(new AnonymousAuthenticationToken("test", userInfoModel, List.of(new RoleGrantedAuthority("test")))));

        User user = userAccessor.get();
        assert user.getUserId().equals(email);
        assert user.getUsername().equals(username);
    }
}
