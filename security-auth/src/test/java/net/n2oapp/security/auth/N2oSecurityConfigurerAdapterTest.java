package net.n2oapp.security.auth;

import net.n2oapp.security.auth.common.AuthoritiesPrincipalExtractor;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = TestApplication.class)
@Import(SecurityConfig.class)
public class N2oSecurityConfigurerAdapterTest {

    @Autowired
    private ApplicationContext applicationContext;

    @MockBean
    private AuthoritiesPrincipalExtractor authoritiesPrincipalExtractor;

    @Test
    public void beansInitTest() {
        assertNotNull(applicationContext.getBean(SecuritySimplePermissionApi.class));
        assertNotNull(applicationContext.getBean(SpringSecurityUserContext.class));
    }

}