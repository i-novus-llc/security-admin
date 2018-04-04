package net.n2oapp.framework.security.auth.oauth2;

import net.n2oapp.security.auth.authority.RoleGrantedAuthority;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.OAuth2RestOperations;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.resource.BaseOAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.OAuth2Request;
import org.springframework.security.oauth2.provider.token.ResourceServerTokenServices;

import java.util.*;
import java.util.stream.Collectors;

/**
 * {@link org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices}
 *
 */
public class UserInfoTokenServices implements ResourceServerTokenServices {
	private static final String[] PRINCIPAL_KEYS = new String[] {"user", "username",
			"userid", "user_id", "login", "id", "name", "sub"};

	private final String userInfoEndpointUrl;

	private final String clientId;

	private OAuth2RestOperations restTemplate;

	private String tokenType = DefaultOAuth2AccessToken.BEARER_TYPE;

//	private AuthoritiesExtractor authoritiesExtractor = new FixedAuthoritiesExtractor();

//	private PrincipalExtractor principalExtractor = new FixedPrincipalExtractor();


	public UserInfoTokenServices(String userInfoEndpointUrl, String clientId) {
		this.userInfoEndpointUrl = userInfoEndpointUrl;
		this.clientId = clientId;
	}

	public void setTokenType(String tokenType) {
		this.tokenType = tokenType;
	}

	public void setRestTemplate(OAuth2RestOperations restTemplate) {
		this.restTemplate = restTemplate;
	}
	//	}
	//		this.authoritiesExtractor = authoritiesExtractor;
	//		Assert.notNull(authoritiesExtractor, "AuthoritiesExtractor must not be null");
//	public void setAuthoritiesExtractor(AuthoritiesExtractor authoritiesExtractor) {

	//	}
	//		this.principalExtractor = principalExtractor;
	//		Assert.notNull(principalExtractor, "PrincipalExtractor must not be null");
//	public void setPrincipalExtractor(PrincipalExtractor principalExtractor) {

//	@Override

	public OAuth2Authentication loadAuthentication(String accessToken)
			throws AuthenticationException, InvalidTokenException {
		Map<String, Object> map = getMap(this.userInfoEndpointUrl, accessToken);
		if (map.containsKey("error")) {
			throw new InvalidTokenException(accessToken);
		}
		return extractAuthentication(map);
	}

	private OAuth2Authentication extractAuthentication(Map<String, Object> map) {
		Object principal = getPrincipal(map);
//		List<GrantedAuthority> authorities = this.authoritiesExtractor
//				.extractAuthorities(map);
		OAuth2Request request = new OAuth2Request(null, this.clientId, null, true, null,
				null, null, null, null);
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
				principal, "N/A", getAuthorities(map));
		token.setDetails(map);
		return new OAuth2Authentication(request, token);
	}

	private List<GrantedAuthority> getAuthorities(Map<String, Object> map) {
		Object roles = map.get("roles");
		if (roles instanceof Collection) {
			Collection<String> roleList = (Collection<String>) roles;
			return roleList.stream().map(RoleGrantedAuthority::new).collect(Collectors.toList());
		}
		return Collections.singletonList(new RoleGrantedAuthority("ROLE_USER"));
	}

	/**
	 * Return the principal that should be used for the token. The default implementation
	 * @param map the source map
	 * @return the principal or {@literal "unknown"}
	 */
	protected Object getPrincipal(Map<String, Object> map) {
		Object principal = extractPrincipal(map);
		return (principal == null ? "unknown" : principal);
	}

	public Object extractPrincipal(Map<String, Object> map) {
		for (String key : PRINCIPAL_KEYS) {
			if (map.containsKey(key)) {
				return map.get(key);
			}
		}
		return null;
	}

//	@Override
	public OAuth2AccessToken readAccessToken(String accessToken) {
		throw new UnsupportedOperationException("Not supported: read access token");
	}

	@SuppressWarnings({ "unchecked" })
	private Map<String, Object> getMap(String path, String accessToken) {
		try {
			OAuth2RestOperations restTemplate = this.restTemplate;
			if (restTemplate == null) {
				BaseOAuth2ProtectedResourceDetails resource = new BaseOAuth2ProtectedResourceDetails();
				resource.setClientId(this.clientId);
				restTemplate = new OAuth2RestTemplate(resource);
			}
			OAuth2AccessToken existingToken = restTemplate.getOAuth2ClientContext()
					.getAccessToken();
			if (existingToken == null || !accessToken.equals(existingToken.getValue())) {
				DefaultOAuth2AccessToken token = new DefaultOAuth2AccessToken(
						accessToken);
				token.setTokenType(this.tokenType);
				restTemplate.getOAuth2ClientContext().setAccessToken(token);
			}
			return restTemplate.getForEntity(path, Map.class).getBody();
		}
		catch (Exception ex) {
			return Collections.<String, Object>singletonMap("error",
					"Could not fetch user details");
		}
	}

}
