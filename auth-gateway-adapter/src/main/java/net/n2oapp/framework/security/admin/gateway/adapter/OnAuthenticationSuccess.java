package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Map;

/**
 * Событие успешной аутентификации
 */
public class OnAuthenticationSuccess implements ApplicationListener<AuthenticationSuccessEvent> {

    private ClientServerSessionRegistry sessionRegistry;

    public OnAuthenticationSuccess(ClientServerSessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String clientSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        String serverSessionId = (String) ((Map) ((OAuth2Authentication) event.getAuthentication()).getUserAuthentication().getDetails()).get("sid");
        sessionRegistry.registerNewSession(clientSessionId, serverSessionId, event.getAuthentication().getPrincipal());
    }
}
