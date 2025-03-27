package net.n2oapp.framework.security.autoconfigure.userinfo;

import feign.RequestTemplate;
import net.n2oapp.framework.security.autoconfigure.access.AccountContextAutoConfiguration;
import net.n2oapp.framework.security.autoconfigure.access.SecurityAutoConfiguration;
import net.n2oapp.framework.security.autoconfigure.userinfo.mapper.OauthPrincipalToJsonMapper;
import net.n2oapp.security.auth.common.OauthUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(classes = {UserInfoTest.class, TestConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, properties = "spring.security.strategy=GLOBAL")
@EnableAutoConfiguration(exclude = {SecurityAutoConfiguration.class, AccountContextAutoConfiguration.class})
public class UserInfoTest {

    @LocalServerPort
    private int port;

    @Autowired
    private RestClient restClient;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserInfoHeaderHelper userInfoHeaderHelper;

    @Test
    public void userinfoRestClientTest() {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        ResponseEntity<Boolean> nextServiceContextCorrect = restClient.get().uri("http://localhost:" + port).retrieve().toEntity(Boolean.class);
        assertThat(nextServiceContextCorrect.getBody(), is(true));
    }

    @Test
    public void userinfoRestTemplate() {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        ResponseEntity<Boolean> nextServiceContextCorrect = restTemplate.getForEntity("http://localhost:" + port, Boolean.class);
        assertThat(nextServiceContextCorrect.getBody(), is(true));
    }

    @Test
    public void noAuthRestTemplate() {
        ResponseEntity<Boolean> nextServiceContextCorrect = restTemplate.getForEntity("http://localhost:" + port, Boolean.class);
        assertThat(nextServiceContextCorrect.getBody(), is(false));
    }

    @Test
    public void helperRequestTemplateTest() {
        SecurityContextHolder.setContext(new SecurityContextImpl(oAuth2AuthenticationToken()));
        RequestTemplate requestTemplate = new RequestTemplate();
        OauthPrincipalToJsonMapper mapper = new OauthPrincipalToJsonMapper();
        mapper.setUserInfoUserNameOnly(false);
        userInfoHeaderHelper.addUserInfoHeader(requestTemplate,mapper);
        assertEquals(1,requestTemplate.headers().size());
    }

    private OAuth2AuthenticationToken oAuth2AuthenticationToken() {
        OidcIdToken oidcIdToken = new OidcIdToken("test_token_value", Instant.MIN, Instant.MAX, Map.of("sub", "sub"));
        OauthUser oauthUser = new OauthUser("admin", oidcIdToken);
        oauthUser.setEmail("test@i-novus.ru");
        oauthUser.setSurname("Колокольцев");
        OAuth2AuthenticationToken oAuth2AuthenticationToken = new OAuth2AuthenticationToken(oauthUser, null, "test");
        return oAuth2AuthenticationToken;
    }
}
