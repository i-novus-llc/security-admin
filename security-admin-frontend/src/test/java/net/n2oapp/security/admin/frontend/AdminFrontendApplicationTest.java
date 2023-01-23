package net.n2oapp.security.admin.frontend;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(properties = {
        "spring.security.oauth2.client.registration.admin-web.client-secret=fbcf4023-afe0-4ba1-944e-7a0f675b0b98",
        "spring.security.oauth2.client.registration.admin-web.client-id=admin-web",
        "access.keycloak.server-url=https://keycloak8.i-novus.ru/auth"})
public class AdminFrontendApplicationTest {

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() {
        assertThat(context, notNullValue());
    }
}
