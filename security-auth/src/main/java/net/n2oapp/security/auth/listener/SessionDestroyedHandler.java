package net.n2oapp.security.auth.listener;

import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.framework.api.ConfigLogoutEvent;
import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.event.N2oEventBus;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.properties.StaticProperties;
import net.n2oapp.security.auth.UserParamsUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.List;

public class SessionDestroyedHandler implements ApplicationListener<SessionDestroyedEvent> {

    private N2oEventBus eventBus;

    @Override
    public void onApplicationEvent(SessionDestroyedEvent sessionDestroyedEvent) {
        List<SecurityContext> lstSecurityContext = sessionDestroyedEvent.getSecurityContexts();
        for (SecurityContext securityContext : lstSecurityContext) {
            Authentication authentication = securityContext.getAuthentication();
            String sessionId = ((WebAuthenticationDetails)authentication.getDetails()).getSessionId();
            eventBus.publish(createEvent(sessionId, authentication.getPrincipal()));
        }
    }

    private ConfigLogoutEvent createEvent(String sessionId, Object principal) {
        ContextEngine contextEngine = StaticSpringContext.getBean(StaticProperties.getProperty("n2o.context.impl"), ContextEngine.class);
        return new ConfigLogoutEvent(UserParamsUtil.getUsername(principal), (String) contextEngine.get(UserContext.CONTEXT), sessionId);
    }

    public void setEventBus(N2oEventBus eventBus) {
        this.eventBus = eventBus;
    }
}
