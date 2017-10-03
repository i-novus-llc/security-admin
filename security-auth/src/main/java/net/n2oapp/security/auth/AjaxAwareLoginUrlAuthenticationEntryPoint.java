package net.n2oapp.security.auth;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AjaxAwareLoginUrlAuthenticationEntryPoint extends LoginUrlAuthenticationEntryPoint {

    public AjaxAwareLoginUrlAuthenticationEntryPoint(String loginFormUrl) {
        super(loginFormUrl);
    }

    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        if (isAjaxRequest(request) && authException != null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.flushBuffer();
            return;
        }
        super.commence(request, response, authException);
    }

    private static boolean isAjaxRequest(HttpServletRequest request) {
        return "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
    }
}