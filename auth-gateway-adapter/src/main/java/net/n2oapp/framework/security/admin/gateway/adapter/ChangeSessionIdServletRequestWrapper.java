package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

/**
 * Обертка запроса.
 * Учитывает смену id сессии в хранилище сессий
 * {@link ChangeSessionIdAuthenticationStrategy}
 */
public class ChangeSessionIdServletRequestWrapper extends HttpServletRequestWrapper {

    private ClientServerSessionRegistry sessionRegistry;

    public ChangeSessionIdServletRequestWrapper(HttpServletRequest request, ClientServerSessionRegistry sessionRegistry) {
        super(request);
        this.sessionRegistry = sessionRegistry;
    }

    @Override
    public String changeSessionId() {
        String oldClientSessionId = null;
        HttpSession session = getSession(false);
        if (session != null) {
            oldClientSessionId = session.getId();
        }

        String newClientSessionId = super.changeSessionId();

        if (oldClientSessionId != null) {
            sessionRegistry.changeSessionId(newClientSessionId, oldClientSessionId);
        }

        return newClientSessionId;
    }
}
