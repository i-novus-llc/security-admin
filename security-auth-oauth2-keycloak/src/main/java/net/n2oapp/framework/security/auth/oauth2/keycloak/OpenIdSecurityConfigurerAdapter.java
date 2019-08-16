package net.n2oapp.framework.security.auth.oauth2.keycloak;

import net.n2oapp.security.admin.api.service.UserDetailsService;
import net.n2oapp.security.auth.N2oSecurityConfigurerAdapter;
import net.n2oapp.security.auth.context.SpringSecurityUserContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.security.oauth2.client.EnableOAuth2Sso;
import org.springframework.boot.autoconfigure.security.oauth2.client.OAuth2SsoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.LogoutConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Адаптер для настройки SSO аутентификации по протоколу OAuth2 OpenId Connect
 */
public abstract class OpenIdSecurityConfigurerAdapter extends N2oSecurityConfigurerAdapter {

    @Value("${security.oauth2.sso.logout-uri}")
    private String ssoLogoutUri;
    @Autowired
    private OAuth2SsoProperties sso;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        authorize(beforeAuthorize(http));
        configureExceptionHandling(http.exceptionHandling());
        configureLogout(http.logout());
        http.csrf().disable();
    }


    @Override
    public SpringSecurityUserContext springSecurityUserContext() {
        return new SpringSecurityUserContext() {
            @Override
            public Object get(String param) {
                if ("token".equals(param)) {
                    OAuth2AuthenticationDetails details = getAuthenticationDetails();
                    if (details != null) {
                        return details.getTokenValue();
                    }
                }
                return super.get(param);
            }
        };
    }

    protected LogoutConfigurer<HttpSecurity> configureLogout(LogoutConfigurer<HttpSecurity> logout) throws Exception {
        if (ssoLogoutUri == null)
            return logout.logoutSuccessUrl("/logout");
        else {
            AutoRedirectLogoutSuccessHandler logoutSuccessHandler = new AutoRedirectLogoutSuccessHandler();
            logoutSuccessHandler.setDefaultTargetUrl(ssoLogoutUri);
            return logout.logoutSuccessHandler(logoutSuccessHandler);
        }
    }

    private OAuth2AuthenticationDetails getAuthenticationDetails() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context != null) {
            Authentication authentication = context.getAuthentication();
            if (authentication != null) {
                Object details = authentication.getDetails();
                if (details instanceof OAuth2AuthenticationDetails) {
                    return (OAuth2AuthenticationDetails) details;
                }
            }
        }
        return null;
    }

    /**
     * Gives support of login-like behaviour. Makes possible redirect back to application from sso server without any manual back-url configuration.
     */
    protected static class AutoRedirectLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
        /**
         * Adds server and servlet base path part of url from request to redirect parameter value.<br/>
         * Base target url should end with parameter "redirect_uri=".<br/>
         * For example, if request contains "http://mydomain.com/app/base/path/some/service" then "http://mydomain.com/app/base/path" will be added to target url.
         * @param request
         * @param response
         * @return Extended target URL.
         */
        @Override
        protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
            StringBuffer requestURL = request.getRequestURL();
            return super.determineTargetUrl(request, response) + requestURL.substring(0, requestURL.lastIndexOf(request.getServletPath()));
        }
    }
}
