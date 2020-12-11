package net.n2oapp.security.admin.auth.server.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.AbstractConfigurableTemplateResolver;
import org.thymeleaf.templateresolver.UrlTemplateResolver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class BadCredentialsExceptionExceptionHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        try {
            if (exception instanceof BadCredentialsException) {

                TemplateEngine engine = new TemplateEngine();
                AbstractConfigurableTemplateResolver resolver = new UrlTemplateResolver();
                resolver.setTemplateMode(TemplateMode.HTML);
                resolver.setCharacterEncoding("UTF-8");
                engine.setTemplateResolver(resolver);

                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                response.setContentType("text/html;charset=UTF-8");

                WebContext context = new WebContext(request, response, request.getServletContext());
                context.setVariable("errorMessage", exception.getMessage());
                String errorPage = engine.process("classpath:public/wrongClientCredentials.html", context);
                response.setContentLength(errorPage.length());
                response.getOutputStream().println(errorPage);
                response.getOutputStream().flush();
            } else super.onAuthenticationFailure(request, response, exception);
        } catch (Exception e) {
            System.out.println(e);
        }
    }
}
