package net.n2oapp.security.auth.common.context;

import net.n2oapp.security.auth.common.GatewayPrincipalExtractor;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.autoconfigure.security.oauth2.resource.AuthoritiesExtractor;
import org.springframework.boot.autoconfigure.security.oauth2.resource.PrincipalExtractor;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.util.Assert;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class ContextUserInfoTokenServices {

    protected final Log logger = LogFactory.getLog(getClass());

    private final String userInfoEndpointUrl;

    private final String clientId;

    private OAuth2RestTemplate restTemplate;

    private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

    private AuthoritiesExtractor authoritiesExtractor = new GatewayPrincipalExtractor();

    private PrincipalExtractor principalExtractor = new GatewayPrincipalExtractor();

    public ContextUserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
        this.clientId = clientId;
    }

    public void setRestTemplate(OAuth2RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {
        Assert.notNull(authoritiesExtractor, "AuthoritiesExtractor must not be null");
        this.authoritiesExtractor = authoritiesExtractor;
    }

    public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {
        Assert.notNull(principalExtractor, "PrincipalExtractor must not be null");
        this.principalExtractor = principalExtractor;
    }

    public OAuth2Authentication loadAuthentication(String accessToken, Integer accountId)
            throws AuthenticationException, InvalidTokenException {
        Map<String, Object> map = getMap(this.userInfoEndpointUrl + "/" + accountId, accessToken);
        if (map.containsKey("error")) {
            if (this.logger.isDebugEnabled()) {
                this.logger.debug("userinfo returned error: " + map.get("error"));
            }
            throw new InvalidTokenException(accessToken);
        }
        return extractAuthentication(map);
    }

    private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
        Object principal = getPrincipal(map);
        List<GrantedAuthority> authorities = this.authoritiesExtractor.extractAuthorities(map);
        OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null, null, null, null, null);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(principal, "N/A",
                authorities);
        token.setDetails(map);
        return new OAuth2Authentication(request, token);
    }

    protected Object getPrincipal(Map<String, Object> map) {
        Object principal = this.principalExtractor.extractPrincipal(map);
        return (principal == null ? "unknown" : principal);
    }

    @SuppressWarnings({"unchecked"})
    private Map<String, Object> getMap(String path, String accessToken) {
        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Getting user info from: " + path);
        }
        try {
            OAuth2RestTemplate restTemplate = this.restTemplate;
            if (restTemplate == null) {
                BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
                resource.setClientId(this.clientId);
                restTemplate = new OAuth2RestTemplate(resource);
                restTemplate.getInterceptors().add((request, body, execution) -> {
                    request.getHeaders().setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
                    return execution.execute(request, body);
                });
            }
            OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext().getAccessToken();
            if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
                DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(accessToken);
                token.setTokenType(this.tokenType);
                restTemplate.getOAuth2ClientContext().setAccessToken(token);
            }
            return restTemplate.getForEntity(path, Map.class).getBody();
        } catch (Exception ex) {
            this.logger.warn("Could not fetch user details: " + ex.getClass() + ", " + ex.getMessage());
            return Collections.<String, Object>singletonMap("error", "Could not fetch user details");
        }
    }
}
