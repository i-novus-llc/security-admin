package net.n2oapp.security.admin.auth.server.exception;

import net.n2oapp.security.admin.impl.exception.UserNotFoundAuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.WebAttributes;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) {
        try {
            if (exception instanceof BadCredentialsException || exception instanceof AccountExpiredException) {
                writePage(request, response, exception);
            } else if (exception instanceof UserNotFoundAuthenticationException) {
                request.setAttribute(WebAttributes.ACCESS_DENIED_403,
                        exception);

                response.setStatus(HttpStatus.FORBIDDEN.value());

                RequestDispatcher dispatcher = request.getRequestDispatcher("/403.html");
                dispatcher.forward(request, response);
            }
            else super.onAuthenticationFailure(request, response, exception);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    private void writePage(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        TemplateEngine engine = new TemplateEngine();
        AbstractConfigurableTemplateResolver resolver = new UrlTemplateResolver();
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setCharacterEncoding("UTF-8");
        engine.setTemplateResolver(resolver);

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.TEXT_HTML_VALUE);

        WebContext context = new WebContext(request, response, request.getServletContext());
        context.setVariable("errorMessage", exception.getMessage());
        String errorPage = engine.process("classpath:public/badClientCredentials.html", context);

        response.setHeader("Content-Type", "text/html;charset=UTF-8");
        response.getWriter().write(errorPage);
    }
}
