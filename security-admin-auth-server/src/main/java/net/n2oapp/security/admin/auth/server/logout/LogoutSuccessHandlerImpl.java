package net.n2oapp.security.admin.auth.server.logout;

import net.n2oapp.security.auth.common.LogoutHandler;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Обработчик успешного логаута
 */
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {

    private List<LogoutHandler> logoutHandlers;
    private LogoutSuccessHandler logoutSuccessHandler;

    static final String EXT_SYS_ATTR = LogoutSuccessHandlerImpl.class.toString() + "SYSTEM";

    public LogoutSuccessHandlerImpl(List<LogoutHandler> logoutHandlers, String keycloakLogoutUrl, String esiaLogoutUrl) {
        this.logoutHandlers = logoutHandlers;
        this.logoutSuccessHandler = new RedirectLogoutRequestHandler(keycloakLogoutUrl, esiaLogoutUrl);
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

        logoutSuccessHandler.onLogoutSuccess(request, response, authentication);
        logoutHandlers.forEach(h -> CompletableFuture.runAsync(() -> h.doLogout(authentication)));
    }
}
