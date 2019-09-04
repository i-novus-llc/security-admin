package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;

/**
 * Слушатель события смены идентификатора сессии
 */
public class ChangeSessionIdListener implements ApplicationListener<SessionFixationProtectionEvent> {

    private SessionRegistry sessionRegistry;

    public ChangeSessionIdListener(SessionRegistry sessionRegistry) {
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public void onApplicationEvent(SessionFixationProtectionEvent event) {
        sessionRegistry.registerNewSession(event.getNewSessionId(), sessionRegistry.getSessionInformation(event.getOldSessionId()).getPrincipal());
        sessionRegistry.removeSessionInformation(event.getOldSessionId());
    }
}
