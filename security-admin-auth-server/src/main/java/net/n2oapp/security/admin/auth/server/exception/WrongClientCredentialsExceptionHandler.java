package net.n2oapp.security.admin.auth.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class WrongClientCredentialsExceptionHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        if (exception instanceof BadCredentialsException) {
            request.setAttribute(WebAttributes.AUTHENTICATION_EXCEPTION,
                    exception);

            response.setStatus(HttpStatus.UNAUTHORIZED.value());

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WrongClientCredentials.html");
            dispatcher.forward(request, response);
        } else super.onAuthenticationFailure(request, response, exception);
    }
}
