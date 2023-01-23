package net.n2oapp.security.auth.context;

import net.n2oapp.security.auth.SpringUserContextWithToken;
import net.n2oapp.security.auth.common.OauthUser;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.catchThrowable;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
public class SpringSecurityUserContextTest {
    private final SpringSecurityUserContext springSecurityUserContext = new SpringSecurityUserContext();
    private final SpringSecurityUserContext openIdContext = new SpringUserContextWithToken();

    @Test
    public void get() {
        Map<String, Object> baseParams = new HashMap<>();
        baseParams.put("testKey", "TestObject");
        Object baseParam = springSecurityUserContext.get("testKey", baseParams);
        assertEquals("TestObject", baseParam);
        baseParam = springSecurityUserContext.get("notExists", baseParams);
        assertNull(baseParam);
    }

    @Test
    public void set() {
        Map<String, Object> baseParams = new HashMap<>();
        Map<String, Object> context = new HashMap<>();
        Throwable thrown = catchThrowable(() -> springSecurityUserContext.set(context));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
        thrown = catchThrowable(() -> springSecurityUserContext.set(context, baseParams));
        assertThat(thrown).isInstanceOf(UnsupportedOperationException.class);
    }

    @Test
    public void getToken() {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        String tokenValue = (String) openIdContext.get("token");
        assertThat(tokenValue).isEqualTo("test_token_value");
    }

    private OAuth2AuthenticationToken oAuth2AuthenticationToken() {
        OidcIdToken oidcIdToken = new OidcIdToken("test_token_value", Instant.MIN, Instant.MAX, Map.of("sub", "sub"));
        OauthUser oauthUser = new OauthUser("admin", oidcIdToken);
        oauthUser.setEmail("test@i-novus.ru");
        oauthUser.setSurname("admin");
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(oauthUser, null, "test");
        return oAuth2AuthenticationToken;
    }
}