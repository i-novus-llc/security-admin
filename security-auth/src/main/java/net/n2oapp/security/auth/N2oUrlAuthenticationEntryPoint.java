package net.n2oapp.security.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class N2oUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    private String n2oUrl;

    public N2oUrlAuthenticationEntryPoint(String loginFormUrl, String n2oUrl) {
        super(loginFormUrl);
        this.n2oUrl = n2oUrl;
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        if (isN2oRequest(request) && authException != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return;
        }
        super.commence(request, response, authException);
    }

    private boolean isN2oRequest(HttpServletRequest request) {
        return request.getServletPath().startsWith(n2oUrl);
    }
}