package net.n2oapp.security.admin;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Обработчик успешного выхода с редиректом по заданному адресу
 */
public class RedirectLogoutRequestHandler extends SimpleUrlLogoutSuccessHandler {

    private String targetUrl;

    public RedirectLogoutRequestHandler(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String redirectUri = request.getParameter("redirect_uri");
        return StringUtils.isNotEmpty(redirectUri) ? targetUrl + "?redirect_uri=" + redirectUri : targetUrl;
    }
}
