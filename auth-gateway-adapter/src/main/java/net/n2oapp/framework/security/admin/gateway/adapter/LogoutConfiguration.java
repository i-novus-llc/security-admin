package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.session.ConcurrentSessionFilter;

import javax.servlet.http.HttpServletResponse;

@Configuration
public class LogoutConfiguration {

    @Bean
    public ClientServerSessionRegistry sessionRegistry() {
        return new ClientServerSessionRegistry();
    }

    @Bean
    public OnAuthenticationSuccess onAuthenticationSuccess(ClientServerSessionRegistry sessionRegistry) {
        return new OnAuthenticationSuccess(sessionRegistry);
    }

    @Bean
    public ConcurrentSessionFilter concurrentSessionFilter(SessionRegistry sessionRegistry) {
        return new ConcurrentSessionFilter(sessionRegistry, event -> {
            event.getSessionInformation();
            HttpServletResponse response = event.getResponse();
            response.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
            response.setHeader("Location", event.getRequest().getRequestURL().toString());
        });
    }
}
