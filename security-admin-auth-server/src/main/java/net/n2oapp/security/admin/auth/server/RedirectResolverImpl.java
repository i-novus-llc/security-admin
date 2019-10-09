package net.n2oapp.security.admin.auth.server;

import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Set;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Класс, разрешающий redirect_uri
 */
public class RedirectResolverImpl implements RedirectResolver {

    @Override
    public String resolveRedirect(String requestedRedirect, ClientDetails client) throws OAuth2Exception {
        Set<String> authorizedGrantTypes = client.getAuthorizedGrantTypes();
        if (authorizedGrantTypes == null || authorizedGrantTypes.isEmpty()) {
            throw new InvalidGrantException("A client must have at least one authorized grant type.");
        }

        if (authorizedGrantTypes.stream().noneMatch(grantType -> Set.of("authorization_code", "implicit").contains(grantType))) {
            throw new InvalidGrantException(
                    "A redirect_uri can only be used by implicit or authorization_code grant types.");
        }

        Set<String> registeredRedirectUris = client.getRegisteredRedirectUri();
        if (isEmpty(registeredRedirectUris)) {
            throw new InvalidRequestException("At least one redirect_uri must be registered with the client.");
        }

        for (String redirectUri : registeredRedirectUris) {
            if (match(requestedRedirect, redirectUri)) {
                return requestedRedirect;
            }
        }

        throw new RedirectMismatchException("Invalid redirect: " + requestedRedirect
                + " does not match one of the registered values.");
    }

    protected boolean match(String requested, String registered) {

        if (requested != null && registered != null && registered.endsWith("*")
                && requested.startsWith(registered.substring(0, registered.indexOf("*")))) {
            return true;
        }

        UriComponents requestedRedirectUri = UriComponentsBuilder.fromUriString(requested).build();
        String requestedRedirectUriScheme = (requestedRedirectUri.getScheme() != null ? requestedRedirectUri.getScheme() : "");
        String requestedRedirectUriHost = (requestedRedirectUri.getHost() != null ? requestedRedirectUri.getHost() : "");
        String requestedRedirectUriPath = (requestedRedirectUri.getPath() != null ? requestedRedirectUri.getPath() : "");

        UriComponents registeredRedirectUri = UriComponentsBuilder.fromUriString(registered).build();
        String registeredRedirectUriScheme = (registeredRedirectUri.getScheme() != null ? registeredRedirectUri.getScheme() : "");
        String registeredRedirectUriHost = (registeredRedirectUri.getHost() != null ? registeredRedirectUri.getHost() : "");
        String registeredRedirectUriPath = (registeredRedirectUri.getPath() != null ? registeredRedirectUri.getPath() : "");


        return ("*".equals(registeredRedirectUriScheme) || registeredRedirectUriScheme.equals(requestedRedirectUriScheme))
                && ("*".equals(registeredRedirectUriHost) || registeredRedirectUriHost.equals(requestedRedirectUriHost))
                && registeredRedirectUriPath.equals(requestedRedirectUriPath);
    }
}
