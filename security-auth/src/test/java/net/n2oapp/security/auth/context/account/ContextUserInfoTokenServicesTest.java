package net.n2oapp.security.auth.context.account;

import net.n2oapp.security.auth.common.OauthUser;
import net.n2oapp.security.auth.common.PropertySourceAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = PropertySourceAutoConfiguration.class)
public class ContextUserInfoTokenServicesTest {

    @Mock
    private RestTemplate oAuth2RestTemplate;

    @Test
    public void testLoadAuthentication() {
        ContextUserInfoTokenServices tokenServices = new ContextUserInfoTokenServices("userInfoUri");
        tokenServices.setRestTemplate(oAuth2RestTemplate);
        Mockito.when(oAuth2RestTemplate.getForEntity("userInfoUri/1", Map.class)).thenReturn(response());
        OAuth2AuthenticationToken oAuth2Authentication = tokenServices.loadAccountAuthentication(1, oAuth2AuthenticationToken());
        OauthUser user = (OauthUser) oAuth2Authentication.getPrincipal();
        assertThat(user.getUserName(), is("admin"));
        assertThat(user.getEmail(), is("test@i-novus.ru"));
        assertThat(user.getSurname(), is("admin"));
        assertThat(user.getAccountId(), is("1"));

        assertThat(oAuth2Authentication.getAuthorities().size(), is(5));
        assertTrue(oAuth2Authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_testRole1")));
        assertTrue(oAuth2Authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_testRole2")));
        assertTrue(oAuth2Authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("PERMISSION_testPermission1")));
        assertTrue(oAuth2Authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("PERMISSION_testPermission2")));
        assertTrue(oAuth2Authentication.getAuthorities().stream().anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("SYSTEM_testSystems1")));
    }

    private ResponseEntity response() {
        Map body = new HashMap();
        body.put("permissions", List.of("testPermission1", "testPermission2"));
        body.put("systems", List.of("testSystems1"));
        body.put("roles", List.of("testRole1", "testRole2"));
        body.put("accountId", "1");

        ResponseEntity responseEntity = new ResponseEntity(body, HttpStatus.OK);

        return responseEntity;
    }

    private OAuth2AuthenticationToken oAuth2AuthenticationToken() {
        OidcIdToken oidcIdToken = new OidcIdToken("token_value", Instant.MIN, Instant.MAX, Map.of("sub", "sub"));
        OauthUser oauthUser = new OauthUser("admin", oidcIdToken);
        oauthUser.setEmail("test@i-novus.ru");
        oauthUser.setSurname("admin");
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(oauthUser, null, "test");
        return oAuth2AuthenticationToken;
    }
}
