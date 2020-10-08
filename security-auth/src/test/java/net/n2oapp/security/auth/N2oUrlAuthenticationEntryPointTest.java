package net.n2oapp.security.auth;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.ServletException;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@ExtendWith(SpringExtension.class)
public class N2oUrlAuthenticationEntryPointTest {
    private final N2oUrlAuthenticationEntryPoint authenticationEntryPoint = new N2oUrlAuthenticationEntryPoint("loginForm", "n2oUrl");

    @Test
    public void commence() {
        try {
            MockHttpServletRequest request = new MockHttpServletRequest();
            MockHttpServletResponse response = new MockHttpServletResponse();
            request.setServletPath("n2oUrlServletPath");
            BadCredentialsException authException = new BadCredentialsException("Error");

            authenticationEntryPoint.commence(request, response, authException);
            assertEquals(401 ,response.getStatus());

            response = new MockHttpServletResponse();
            authException = null;
            authenticationEntryPoint.commence(request, response, authException);
            assertEquals(302 ,response.getStatus());
        } catch (IOException | ServletException e) {
            fail();
        }
    }
}