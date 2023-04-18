package net.n2oapp.security.auth.context.account;

import net.n2oapp.security.auth.common.OauthUser;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static net.n2oapp.security.auth.common.UserParamsUtil.extractAuthorities;

/**
 * Сервис для получения UserInfo с данными контекста
 * ex org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices
 */

public class ContextUserInfoTokenServices {

    private static final String DEPARTMENT = "department";
    private static final String CODE_KEY = "code";
    private static final String NAME_KEY = "name";
    private static final String ORGANIZATION = "organization";
    private static final String REGION = "region";
    private static final String ACCOUNT_ID = "accountId";

    private final String userInfoEndpointUrl;

    protected final Log logger = LogFactory.getLog(getClass());

    private RestTemplate restTemplate;

    public ContextUserInfoTokenServices(String userInfoEndpointUrl) {
        this.userInfoEndpointUrl = userInfoEndpointUrl;
    }

    public void setRestTemplate(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public OAuth2AuthenticationToken loadAccountAuthentication(Integer accountId, Authentication oAuth2AuthenticationToken) {
        Map<String, Object> map = getMap(this.userInfoEndpointUrl + "/" + accountId);
        return extractAuthentication(map, oAuth2AuthenticationToken);
    }

    protected OAuth2AuthenticationToken extractAuthentication(Map<String, Object> map, Authentication authentication) {
        OAuth2AuthenticationToken oAuth2AuthenticationToken = (OAuth2AuthenticationToken) authentication;
        OauthUser currentOauthUser = (OauthUser) authentication.getPrincipal();

        List<GrantedAuthority> grantedAuthorities = extractAuthorities(map);
        OauthUser oauthUser = new OauthUser(currentOauthUser, grantedAuthorities);
        enrichWithAccountClaims(map, oauthUser);

        return new OAuth2AuthenticationToken(oauthUser, grantedAuthorities, oAuth2AuthenticationToken.getAuthorizedClientRegistrationId());
    }

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

    protected void enrichWithAccountClaims(Map<String, Object> map, OauthUser user) {
        user.setAccountId((String) map.get(ACCOUNT_ID));

        LinkedHashMap<String, Object> department = (LinkedHashMap) map.get(DEPARTMENT);

        if (department != null) {
            user.setDepartment((String) department.get(CODE_KEY));
            user.setDepartmentName((String) department.get(NAME_KEY));
        }

        LinkedHashMap<String, Object> organization = (LinkedHashMap) map.get(ORGANIZATION);
        if (organization != null)
            user.setOrganization((String) organization.get(CODE_KEY));

        LinkedHashMap<String, Object> region = (LinkedHashMap) map.get(REGION);
        if (region != null)
            user.setRegion((String) region.get(CODE_KEY));
    }
}
