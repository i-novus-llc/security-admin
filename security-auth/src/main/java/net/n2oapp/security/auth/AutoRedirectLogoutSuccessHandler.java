package net.n2oapp.security.auth;

import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Gives support of login-like behaviour. Makes possible redirect back to application from sso server without any manual back-url configuration.
 */
public class AutoRedirectLogoutSuccessHandler extends SimpleUrlLogoutSuccessHandler {
    /**
     * Adds server and servlet base path part of url from request to redirect parameter value.<br/>
     * Base target url should end with parameter "redirect_uri=".<br/>
     * For example, if request contains "http://mydomain.com/app/base/path/some/service" then "http://mydomain.com/app/base/path" will be added to target url.
     *
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