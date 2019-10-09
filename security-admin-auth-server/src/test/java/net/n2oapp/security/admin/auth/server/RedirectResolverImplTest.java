package net.n2oapp.security.admin.auth.server;

import org.junit.Test;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.security.oauth2.common.exceptions.InvalidRequestException;
import org.springframework.security.oauth2.common.exceptions.RedirectMismatchException;
import org.springframework.security.oauth2.provider.endpoint.RedirectResolver;

import java.util.Set;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class RedirectResolverImplTest {
    @Test
    public void test() {
        RedirectResolver redirectResolver = new RedirectResolverImpl();
        GatewayClient client = new GatewayClient();
        try {
            redirectResolver.resolveRedirect("https://google.com", client);
            assert false;
        } catch (InvalidGrantException e) {
            assertThat(e.getMessage(), is("A client must have at least one authorized grant type."));
        }

        client.setAuthorizedGrantTypes(Set.of("authorization_code"));
        try {
            redirectResolver.resolveRedirect("https://google.com", client);
            assert false;
        } catch (InvalidRequestException e) {
            assertThat(e.getMessage(), is("At least one redirect_uri must be registered with the client."));
        }

        client.setRegisteredRedirectUri(Set.of("https://google.com"));
        assertThat(redirectResolver.resolveRedirect("https://google.com", client), is("https://google.com"));

        try {
            redirectResolver.resolveRedirect("not_valid_url", client);
        } catch (RedirectMismatchException e) {
            assertThat(e.getMessage(), is("Invalid redirect: not_valid_url does not match one of the registered values."));
        }

        client.setRegisteredRedirectUri(Set.of("https://google.com/*"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/a", client), is("https://google.com/a"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/a/b", client), is("https://google.com/a/b"));

        client.setRegisteredRedirectUri(Set.of("https://google.com/a/*"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/a/b", client), is("https://google.com/a/b"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/a/b/c", client), is("https://google.com/a/b/c"));

        client.setRegisteredRedirectUri(Set.of("*://google.com/*"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/a", client), is("https://google.com/a"));
        assertThat(redirectResolver.resolveRedirect("http://google.com/a", client), is("http://google.com/a"));

        client.setRegisteredRedirectUri(Set.of("https://*/abc"));
        assertThat(redirectResolver.resolveRedirect("https://yandex.ru/abc", client), is("https://yandex.ru/abc"));
        assertThat(redirectResolver.resolveRedirect("https://google.com/abc", client), is("https://google.com/abc"));

        client.setRegisteredRedirectUri(Set.of("https://*"));
        assertThat(redirectResolver.resolveRedirect("https://yandex.ru/abc", client), is("https://yandex.ru/abc"));
    }
}
