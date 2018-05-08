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

/**
 * Слушает успешную аутентиикацию, чтобы разослать LoginEvent
 * необходим для корректной работы контекста, кэширования в n2o
 */
public class AuthenticationSuccessListener implements ApplicationListener<AuthenticationSuccessEvent> {

    private N2oEventBus eventBus;

    @Override
    public void onApplicationEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication() == null) return;
        Authentication authentication = event.getAuthentication();
        eventBus.publish(createEvent(authentication));
    }

    private LoginEvent createEvent(Authentication authentication) {
        ContextEngine contextEngine = StaticSpringContext.getBean(ContextEngine.class);
        return new LoginEvent(UserParamsUtil.getSessionId(authentication),
                UserParamsUtil.getUsername(authentication.getPrincipal()),
                (String) contextEngine.get(UserContext.CONTEXT));
    }

    public void setEventBus(N2oEventBus eventBus) {
        this.eventBus = eventBus;
    }
}
