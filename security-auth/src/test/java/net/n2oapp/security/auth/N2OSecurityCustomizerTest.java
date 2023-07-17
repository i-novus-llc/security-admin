package net.n2oapp.security.auth;

import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@Import(SecurityConfig.class)
public class N2OSecurityCustomizerTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    public void beansInitTest() {
        assertNotNull(applicationContext.getBean(SecuritySimplePermissionApi.class));
        assertNotNull(applicationContext.getBean(SpringSecurityUserContext.class));
    }

    @Test
    public void logoutSuccessHandlerTest(){
        AutoRedirectLogoutSuccessHandler handler = new AutoRedirectLogoutSuccessHandler();
        handler.setDefaultTargetUrl("http://test.test/logout?redirect_uri=");
        String targetUrl = handler.determineTargetUrl(new MockHttpServletRequest("GET", "/logout"), new MockHttpServletResponse());
        assertEquals("http://test.test/logout?redirect_uri=http://localhost/logout", targetUrl);
    }
}