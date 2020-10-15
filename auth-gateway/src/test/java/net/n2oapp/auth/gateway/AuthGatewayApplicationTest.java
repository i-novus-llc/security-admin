package net.n2oapp.auth.gateway;

import net.n2oapp.auth.gateway.scheduled.SynchronizationInfo;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

//нужно настроить соединение с бд
@Disabled
@SpringBootTest
public class AuthGatewayApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    public void contextLoads() {
        assertThat(context, notNullValue());

        Map<String, SynchronizationInfo> beans = context.getBeansOfType(SynchronizationInfo.class);
        assertThat(beans.containsKey("appSysExportJobAndTrigger"), is(true));
        assertThat(beans.containsKey("regionJobAndTrigger"), is(true));
        assertThat(beans.containsKey("departmentJobAndTrigger"), is(true));
        assertThat(beans.containsKey("organizationJobAndTrigger"), is(true));
    }
}
