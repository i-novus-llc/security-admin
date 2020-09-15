package net.n2oapp.framework.security.admin.gateway.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jwt.Jwt;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Сервлет обработки запроса на выход
 */
public class BackChannelLogoutServlet extends HttpServlet {

    private static final String USERNAME = "username";

    private ObjectMapper mapper = new ObjectMapper();

    private SessionRegistry sessionRegistry;
    private final JwtHelperHolder jwtHelperHolder;

    public BackChannelLogoutServlet(SessionRegistry sessionRegistry, JwtHelperHolder jwtHelperHolder) {
        this.sessionRegistry = sessionRegistry;
        this.jwtHelperHolder = jwtHelperHolder;
    }

    public BackChannelLogoutServlet(SessionRegistry sessionRegistry, String tokenKeyEndpointUrl) {
        this(sessionRegistry, new JwtHelperHolder(tokenKeyEndpointUrl));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        handleLogout(req);
    }

    private void handleLogout(HttpServletRequest req) {
        String token = req.getParameterMap().get("logout_token")[0];
        Jwt jwt = jwtHelperHolder.decodeAndVerify(token);
        Map<String, Object> claims;
        try {
            claims = mapper.readValue(jwt.getClaims(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new IllegalStateException("Cannot read logout token", e);
        }
        if (checkClaims(claims)) {
            sessionRegistry.getAllSessions(new User(claims.get(USERNAME).toString(), "N/A", Collections.emptyList()), true)
                    .forEach(SessionInformation::expireNow);
        }
    }

    private boolean checkClaims(Map<String, Object> claims) {
        return claims != null && "LOGOUT".equals(claims.get("event")) && claims.get(USERNAME) != null;
    }

}
