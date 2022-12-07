package net.n2oapp.security.account;

import net.n2oapp.security.account.context.ContextUserInfoTokenServices;
import net.n2oapp.security.auth.common.GatewayPrincipalExtractor;
import net.n2oapp.security.auth.common.PropertySourceAutoConfiguration;
import net.n2oapp.security.auth.common.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

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
        ContextUserInfoTokenServices tokenServices = new ContextUserInfoTokenServices("userInfoUri", "clientId");
        tokenServices.setAuthoritiesExtractor(new GatewayPrincipalExtractor());
        tokenServices.setPrincipalExtractor(new GatewayPrincipalExtractor());
        tokenServices.setRestTemplate(oAuth2RestTemplate);
        Mockito.when(oAuth2RestTemplate.getForEntity("userInfoUri/1", Map.class)).thenReturn(response());
        OAuth2Authentication oAuth2Authentication = tokenServices.loadAuthentication(1);
        User user = (User) oAuth2Authentication.getPrincipal();
        assertThat(user.getUsername(), is("admin"));
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
        body.put("surname", "admin");
        body.put("email", "test@i-novus.ru");
        body.put("username", "admin");
        body.put("sid", "F70AD32914B5B53A6476909FF06B9FEC");
        body.put("permissions", List.of("testPermission1", "testPermission2"));
        body.put("systems", List.of("testSystems1"));
        body.put("roles", List.of("testRole1", "testRole2"));
        body.put("accountId", "1");

        ResponseEntity responseEntity = new ResponseEntity(body, HttpStatus.OK);

        return responseEntity;
    }
}
