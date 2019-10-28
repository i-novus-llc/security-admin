package net.n2oapp.security.admin.auth.server.exception;

import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserNotFoundAuthenticationExceptionHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UserNotFoundAuthenticationException) {
            request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                    exception);

            response.setStatus(HttpStatus.FORBIDDEN.value());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/403.html");
            dispatcher.forward(request, response);
        } else super.onAuthenticationFailure(request, response, exception);
    }
}
