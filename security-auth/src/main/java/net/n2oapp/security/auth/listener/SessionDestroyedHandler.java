package net.n2oapp.security.auth.listener;

import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.framework.api.ConfigLogoutEvent;
import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.event.N2oEventBus;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.security.auth.UserParamsUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;

import java.util.List;

/**
 * Обработка удаления сессии
 */
public class SessionDestroyedHandler implements ApplicationListener<SessionDestroyedEvent> {

    private N2oEventBus eventBus;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent) {
        List<SecurityContext> lstSecurityContext = sessionDestroyedEvent.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext) {
            Authentication authentication = securityContext.getAuthentication();
            eventBus.publish(createEvent(authentication));
        }
    }

    private ConfigLogoutEvent createEvent(Authentication authentication) {
        ContextEngine contextEngine = StaticSpringContext.getBean(ContextEngine.class);
        return new ConfigLogoutEvent(UserParamsUtil.getUsername(authentication.getPrincipal()),
                (String) contextEngine.get(UserContext.CONTEXT),
                UserParamsUtil.getSessionId(authentication));
    }

    public void setEventBus(N2oEventBus eventBus) {
        this.eventBus = eventBus;
    }
}
