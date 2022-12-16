package net.n2oapp.security.account.context;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.Map;

/**
 * Сервис для получения UserInfo с данными контекста
 * ex org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
 */

public class ContextUserInfoTokenServices {

    protected final Log logger = LogFactory.getLog(getClass());

    private final String userInfoEndpointUrl;

    private final String clientId;

    private RestTemplate restTemplate;

//    private AuthoritiesExtractor authoritiesExtractor = new GatewayPrincipalExtractor();
//
//    private PrincipalExtractor principalExtractor = new GatewayPrincipalExtractor();

    public ContextUserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

//    public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
//        Assert.notNull(authoritiesExtractor, "AuthoritiesExtractor must not be null");
//        this.authoritiesExtractor = authoritiesExtractor;
//    }
//
//    public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
//        Assert.notNull(principalExtractor, "PrincipalExtractor must not be null");
//        this.principalExtractor = principalExtractor;
//    }
//
//    public OAuth2Authentication loadAuthentication(Integer accountId) {
//        Map<String, Object> map = getMap(this.userInfoEndpointUrl + "/" + accountId);
//        return extractAuthentication(map);
//    }
//
//    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
//        Object principal = getPrincipal(map);
//        OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null, null, null, null, null);
//        List<GrantedAuthority> authorities = this.authoritiesExtractor.extractAuthorities(map);
//        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "N/A", authorities);
//        token.setDetails(map);
//        return new OAuth2Authentication(request, token);
//    }
//
//    protected Object getPrincipal(Map<String, Object> map) {
//        Object principal = this.principalExtractor.extractPrincipal(map);
//        return (principal == null ? "unknown" : principal);
//    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> getMap(String path) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            if (restTemplate == null) {
                restTemplate = new RestTemplate();
                restTemplate.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    return execution.execute(request, body);
                });
            }
            return restTemplate.getForEntity(path, Map.class).getBody();
        } catch (Exception ex) {
            this.logger.warn("Could not fetch user details: " + ex.getClass() + ", " + ex.getMessage());
            throw ex;
        }
    }
}
