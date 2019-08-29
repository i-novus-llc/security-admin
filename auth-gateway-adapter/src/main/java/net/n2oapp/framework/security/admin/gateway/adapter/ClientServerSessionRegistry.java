package net.n2oapp.framework.security.admin.gateway.adapter;

import org.springframework.context.ApplicationListener;
import org.springframework.security.core.session.SessionDestroyedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Реализация журнала сессий spring-security
 */
public class ClientServerSessionRegistry implements SessionRegistry, ApplicationListener<SessionDestroyedEvent> {

    private final ConcurrentMap<Object, Set<String>> principals = new ConcurrentHashMap<>();

    private final Map<String, ClientServerSessionInfo> sessionIds = new ConcurrentHashMap<>();


    public List<Object> getAllPrincipals() {
        return new ArrayList<>(principals.keySet());
    }

    public List<SessionInformation> getAllSessions(Object principal,
                                                   boolean includeExpiredSessions) {
        final Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            return Collections.emptyList();
        }

        List<SessionInformation> list = new ArrayList<>(
                sessionsUsedByPrincipal.size());

        for (String sessionId : sessionsUsedByPrincipal) {
            SessionInformation sessionInformation = getSessionInformation(sessionId);

            if (sessionInformation == null) {
                continue;
            }

            if (includeExpiredSessions || !sessionInformation.isExpired()) {
                list.add(sessionInformation);
            }
        }

        return list;
    }

    public SessionInformation getSessionInformation(String sessionId) {
        return sessionIds.get(sessionId);
    }

    public void onApplicationEvent(SessionDestroyedEvent event) {
        String sessionId = event.getId();
        removeSessionInformation(sessionId);
    }

    public void refreshLastRequest(String sessionId) {
        SessionInformation info = getSessionInformation(sessionId);

        if (info != null) {
            info.refreshLastRequest();
        }
    }

    public void registerNewSession(String sessionId, Object principal) {
        throw new UnsupportedOperationException();
    }

    public void registerNewSession(String clientSessionId, String severSessionId, Object principal) {
        if (getSessionInformation(clientSessionId) != null) {
            removeSessionInformation(clientSessionId);
        }

        sessionIds.put(clientSessionId, new ClientServerSessionInfo(clientSessionId, severSessionId, principal, new Date()));

        Set<String> sessionsUsedByPrincipal = principals.get(principal);

        if (sessionsUsedByPrincipal == null) {
            sessionsUsedByPrincipal = new CopyOnWriteArraySet<>();
            Set<String> prevSessionsUsedByPrincipal = principals.putIfAbsent(principal,
                    sessionsUsedByPrincipal);
            if (prevSessionsUsedByPrincipal != null) {
                sessionsUsedByPrincipal = prevSessionsUsedByPrincipal;
            }
        }

        sessionsUsedByPrincipal.add(clientSessionId);
    }

    public void changeSessionId(String newSessionId, String oldSessionId) {
        registerNewSession(newSessionId, sessionIds.get(oldSessionId).getServerSessionId(), sessionIds.get(oldSessionId).getPrincipal());
        removeSessionInformation(oldSessionId);
    }

    public void removeSessionInformation(String sessionId) {

        SessionInformation info = getSessionInformation(sessionId);

        if (info == null) {
            return;
        }

        sessionIds.remove(sessionId);

        Set<String> sessionsUsedByPrincipal = principals.get(info.getPrincipal());

        if (sessionsUsedByPrincipal == null) {
            return;
        }

        sessionsUsedByPrincipal.remove(sessionId);

        if (sessionsUsedByPrincipal.isEmpty()) {
            principals.remove(info.getPrincipal());
        }
    }

    public void markSessionsAsExpired(String username, String serverSessionId) {
        Set<String> sessions = principals.get(new User(username, "N/A", Collections.emptyList()));
        for (String sessionId : sessions) {
            ClientServerSessionInfo sessionInfo = sessionIds.get(sessionId);
            if (sessionInfo != null && serverSessionId.equals(sessionInfo.getServerSessionId())) {
                sessionInfo.expireNow();
            }
        }
    }
}
