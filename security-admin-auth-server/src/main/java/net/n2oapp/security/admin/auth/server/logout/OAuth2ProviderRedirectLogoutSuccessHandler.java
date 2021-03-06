package net.n2oapp.security.admin.auth.server.logout;

import net.n2oapp.security.auth.common.LogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.apache.commons.lang.StringUtils.isEmpty;

/**
 * Обрабатывает редирект на провайдера аутентификации
 */
public class OAuth2ProviderRedirectLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {

    private static final String EXT_SYS_ATTR = OAuth2ProviderRedirectLogoutSuccessHandler.class.toString() + ".SYSTEM";

    private List<LogoutHandler> logoutHandlers;
    private String keycloakLogoutUrl;
    private String esiaLogoutUrl;


    public OAuth2ProviderRedirectLogoutSuccessHandler(List<LogoutHandler> logoutHandlers,
                                                      String keycloakLogoutUrl,
                                                      String esiaLogoutUrl) {
        this.logoutHandlers = logoutHandlers;
        this.keycloakLogoutUrl = keycloakLogoutUrl;
        this.esiaLogoutUrl = esiaLogoutUrl;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2Authentication
                && ((OAuth2Authentication) authentication).getUserAuthentication() != null) {
            Map<String, Object> details = (Map<String, Object>) ((OAuth2Authentication) authentication).getUserAuthentication().getDetails();
            if (details != null && details.containsKey("system")) {
                request.setAttribute(EXT_SYS_ATTR, details.get("system"));
            }
        }

        super.handle(request, response, authentication);
        logoutHandlers.forEach(h -> CompletableFuture.runAsync(() -> h.doLogout(authentication)));
    }

    /**
     * Определяет, в какой провайдер послать запрос на выход
     */
    @Override
    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
        String extSys = (String) request.getAttribute(EXT_SYS_ATTR);
        String redirectUri = request.getParameter("redirect_uri");
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("ESIA".equals(extSys) ? esiaLogoutUrl : keycloakLogoutUrl);
        if (!isEmpty(redirectUri))
            builder.queryParam("redirect_uri", redirectUri).build().toUriString();

        return builder.build().toUriString();

    }
}
