package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.web.context.request.RequestContextHolder;

/**
 * Событие успешной аутентификации
 */
public class OnAuthenticationSuccess implements ApplicationListener<AuthenticationSuccessEvent> {

    private SessionRegistry sessionRegistry;

    public OnAuthenticationSuccess(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        String clientSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
        sessionRegistry.registerNewSession(clientSessionId, event.getAuthentication().getPrincipal());
    }
}
