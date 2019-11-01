package net.n2oapp.security.admin.auth.server.logout;

import org.springframework.security.web.authentication.logout.SimpleUrlLogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static net.n2oapp.security.admin.auth.server.logout.BackChannelLogoutHandler.EXT_SYS_ATTR;
import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Обработчик успешного выхода с редиректом по заданному адресу
 */
public class RedirectLogoutRequestHandler extends SimpleUrlLogoutSuccessHandler {

    private String keycloakLogoutUrl;
    private String esiaLogoutUrl;

    public RedirectLogoutRequestHandler(String keycloakLogoutUrl, String esiaLogoutUrl) {
        this.keycloakLogoutUrl = keycloakLogoutUrl;
        this.esiaLogoutUrl = esiaLogoutUrl;
    }

    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String extSys = (String) request.getAttribute(EXT_SYS_ATTR);
        String redirectUri = request.getParameter("redirect_uri");
        if (!isEmpty(redirectUri)) {
            redirectUri = "redirect_uri=" + redirectUri;
        }
        if ("ESIA".equals(extSys)) {
            redirectUri = esiaLogoutUrl.contains("?") ? "&" + redirectUri : "?" + redirectUri;
            return !isEmpty(redirectUri) ? esiaLogoutUrl + redirectUri : esiaLogoutUrl;
        }
        return !isEmpty(redirectUri) ? keycloakLogoutUrl + "?" + redirectUri : keycloakLogoutUrl;

    }
}
