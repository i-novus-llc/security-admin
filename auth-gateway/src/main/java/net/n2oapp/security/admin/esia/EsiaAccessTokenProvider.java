package net.n2oapp.security.admin.esia;

import org.springframework.http.HttpHeaders;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2AccessDeniedException;
import org.springframework.security.oauth2.client.resource.OAuth2ProtectedResourceDetails;
import org.springframework.security.oauth2.client.resource.UserApprovalRequiredException;
import org.springframework.security.oauth2.client.resource.UserRedirectRequiredException;
import org.springframework.security.oauth2.client.token.AccessTokenRequest;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;

import java.text.SimpleDateFormat;
import java.util.*;


public class EsiaAccessTokenProvider extends AuthorizationCodeAccessTokenProvider {

    private final Pkcs7Util pkcs7Util;


    public EsiaAccessTokenProvider(Pkcs7Util pkcs7Util) {
        this.pkcs7Util = pkcs7Util;
    }

    @Override
    public OAuth2AccessToken obtainAccessToken(OAuth2ProtectedResourceDetails details, AccessTokenRequest request) throws UserRedirectRequiredException, UserApprovalRequiredException, AccessDeniedException, OAuth2AccessDeniedException {
        AuthorizationCodeResourceDetails resource = (AuthorizationCodeResourceDetails) details;

        if (request.getAuthorizationCode() == null) {
            if (request.getStateKey() == null) {
                throw getRedirectForAuthorization(resource, request);
            }
            obtainAuthorizationCode(resource, request);
        }
        return retrieveToken(request, resource, getParametersForTokenRequest(resource, request),
                new HttpHeaders());
    }

    private UserRedirectRequiredException getRedirectForAuthorization(AuthorizationCodeResourceDetails resource,
                                                                      AccessTokenRequest request) {
        TreeMap<String, String> requestParameters = new TreeMap<String, String>();
        requestParameters.put("response_type", "code"); // oauth2 spec, section 3
        requestParameters.put("client_id", resource.getClientId());

        String redirectUri = resource.getRedirectUri(request);
        if (redirectUri != null) {
            requestParameters.put("redirect_uri", redirectUri);
        }

        String stateKey = UUID.randomUUID().toString();
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z").format(new Date());
        String scope = StringUtils.collectionToDelimitedString(resource.getScope(), " ");
        String secret = pkcs7Util.getUrlSafeSign(scope + timestamp + resource.getClientId() + stateKey);

        requestParameters.put("scope", scope);
        requestParameters.put("timestamp", timestamp);
        requestParameters.put("access_type", "online");
        requestParameters.put("client_secret", secret);

        UserRedirectRequiredException redirectException = new UserRedirectRequiredException(
                resource.getUserAuthorizationUri(), requestParameters);

        redirectException.setStateKey(stateKey);
        request.setStateKey(stateKey);
        redirectException.setStateToPreserve(redirectUri);
        request.setPreservedState(redirectUri);

        return redirectException;
    }

    private MultiValueMap<String, String> getParametersForTokenRequest(AuthorizationCodeResourceDetails resource,
                                                                       AccessTokenRequest request) {
        String timestamp = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss Z").format(new Date());
        String scope = StringUtils.collectionToDelimitedString(resource.getScope(), " ");
        String secret = pkcs7Util.getUrlSafeSign(scope + timestamp + resource.getClientId() + request.getStateKey());

        MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>();
        form.set("client_id", resource.getClientId());
        form.set("code", request.getAuthorizationCode());
        form.set("grant_type", "authorization_code");
        form.set("client_secret", secret);
        form.set("state", request.getStateKey());
        form.set("scope", scope);
        form.set("timestamp", timestamp);
        form.set("token_type", "Bearer");

        Object preservedState = request.getPreservedState();
        // The token endpoint has no use for the state so we don't send it back, but we are using it
        // for CSRF detection client side...
        if (preservedState == null) {
            throw new InvalidRequestException(
                    "Possible CSRF detected - state parameter was required but no state could be found");
        }

        // Extracting the redirect URI from a saved request should ignore the current URI, so it's not simply a call to
        // resource.getRedirectUri()
        String redirectUri = null;
        // Get the redirect uri from the stored state
        if (preservedState instanceof String) {
            // Use the preserved state in preference if it is there
            // TODO: treat redirect URI as a special kind of state (this is a historical mini hack)
            redirectUri = String.valueOf(preservedState);
        } else {
            redirectUri = resource.getRedirectUri(request);
        }

        if (redirectUri != null && !"NONE".equals(redirectUri)) {
            form.set("redirect_uri", redirectUri);
        }

        return form;

    }

}
