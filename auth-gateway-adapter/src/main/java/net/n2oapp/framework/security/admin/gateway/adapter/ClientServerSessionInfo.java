package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.security.core.session.SessionInformation;

import java.util.Date;

/**
 * Связь клиентской и серверной сессий
 */
public class ClientServerSessionInfo extends SessionInformation {

    private String serverSessionId;

    public ClientServerSessionInfo(String clientSessionId, String serverSessionId, Object principal, Date lastRequest) {
        super(principal, clientSessionId, lastRequest);
        this.serverSessionId = serverSessionId;
    }

    public String getServerSessionId() {
        return serverSessionId;
    }
}
