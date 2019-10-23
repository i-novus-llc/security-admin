package net.n2oapp.security.admin.auth.server.exception;

import net.n2oapp.security.admin.impl.exception.UserNotFoundOauthException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class UserNotFoundOauthExceptionHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof UserNotFoundOauthException) {
            request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                    exception);

            // Set the 403 status code.
            response.setStatus(HttpStatus.FORBIDDEN.value());

            // forward to error page.
            RequestDispatcher dispatcher = request.getRequestDispatcher("/403.html");
            dispatcher.forward(request, response);
        } else super.onAuthenticationFailure(request, response, exception);
    }
}
