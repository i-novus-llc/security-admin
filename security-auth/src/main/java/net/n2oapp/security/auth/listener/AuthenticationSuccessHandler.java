package net.n2oapp.security.auth.listener;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private String successUrl;

    public void setSuccessUrl(String successUrl) {
        this.successUrl = successUrl;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        return successUrl;
    }
}
