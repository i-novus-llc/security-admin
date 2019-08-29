package net.n2oapp.framework.security.admin.gateway.adapter;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.RsaVerifier;
import org.springframework.security.jwt.crypto.sign.SignatureVerifier;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

/**
 * Фильтр обработки запроса на выход
 */
public class BackChannelLogoutFilter extends GenericFilterBean implements InitializingBean {

    private static final String USERNAME = "username";
    private static final String SID = "sid";
    private static final String BACKCHANNEL_LOGOUT = "backchannel_logout";

    private ObjectMapper mapper = new ObjectMapper();
    private RestTemplate restTemplate = new RestTemplate();
    private volatile SignatureVerifier verifier;

    private ClientServerSessionRegistry sessionRegistry;
    private String tokenKeyEndpointUrl;

    public BackChannelLogoutFilter(ClientServerSessionRegistry sessionRegistry, String tokenKeyEndpointUrl) {
        this.sessionRegistry = sessionRegistry;
        this.tokenKeyEndpointUrl = tokenKeyEndpointUrl;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (isBackChannelLogout(req)) {
            handleLogout(req);
            return;
        }

        chain.doFilter(new ChangeSessionIdServletRequestWrapper(req, sessionRegistry), response);
    }

    private boolean isBackChannelLogout(HttpServletRequest request) {
        return "POST".equals(request.getMethod()) && request.getServletPath().endsWith(BACKCHANNEL_LOGOUT);
    }

    private void handleLogout(HttpServletRequest req) {
        String token = req.getParameterMap().get("logout_token")[0];
        Jwt jwt = JwtHelper.decodeAndVerify(token, getVerifier());
        Map<String, Object> claims;
        try {
            claims = mapper.readValue(jwt.getClaims(), new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        if (checkClaims(claims)) {
            sessionRegistry.markSessionsAsExpired(claims.get(USERNAME).toString(), claims.get(SID).toString());
        }

    }

    private boolean checkClaims(Map<String, Object> claims) {
        return claims != null && "LOGOUT".equals(claims.get("event")) && claims.get(USERNAME) != null;
    }

    private synchronized SignatureVerifier getVerifier() {
        if (verifier == null) {
            Map response = restTemplate.getForObject(tokenKeyEndpointUrl, Map.class);
            if (response != null && response.get("value") != null)
                verifier = new RsaVerifier((String) response.get("value"));
        }

        return verifier;
    }
}
