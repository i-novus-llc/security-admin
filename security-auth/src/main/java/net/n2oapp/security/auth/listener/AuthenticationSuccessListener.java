package net.n2oapp.security.auth.listener;

import net.n2oapp.context.StaticSpringContext;
import net.n2oapp.framework.api.context.ContextEngine;
import net.n2oapp.framework.api.event.LoginEvent;
import net.n2oapp.framework.api.event.N2oEventBus;
import net.n2oapp.framework.api.user.UserContext;
import net.n2oapp.properties.StaticProperties;
import net.n2oapp.security.auth.UserParamsUtil;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private N2oEventBus eventBus;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() == null) return;
        Authentication authentication = event.getAuthentication();
        String sessionId = ((WebAuthenticationDetails)authentication.getDetails()).getSessionId();
        eventBus.publish(createEvent(sessionId, authentication.getPrincipal()));
    }

    private LoginEvent createEvent(String sessionId, Object principal) {
        ContextEngine contextEngine = StaticSpringContext.getBean(StaticProperties.getProperty("n2o.context.impl"), ContextEngine.class);
        return new LoginEvent(sessionId, UserParamsUtil.getUsername(principal), (String) contextEngine.get(UserContext.CONTEXT));
    }

    public void setEventBus(N2oEventBus eventBus) {
        this.eventBus = eventBus;
    }
}
