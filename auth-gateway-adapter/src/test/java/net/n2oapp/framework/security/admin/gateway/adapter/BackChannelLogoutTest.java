package net.n2oapp.framework.security.admin.gateway.adapter;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.jwt.Jwt;
import org.springframework.test.context.TestPropertySource;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(classes = BackChannelLogoutTest.class,
        properties = {"spring.cloud.consul.enabled=false"},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
@SpringBootApplication
public class BackChannelLogoutTest {

    @LocalServerPort
    private String port;

    @Autowired
    private SessionRegistry sessionRegistry;

    @MockBean
    private JwtVerifier jwtVerifier;

    @Test
    public void test() {
        Jwt jwt = new LogoutToken();
        when(jwtVerifier.decodeAndVerify(any())).thenReturn(jwt);
        RestTemplate restTemplate = new RestTemplate();
        User user = new User("test_user", "qwerty", new ArrayList<>());
        sessionRegistry.registerNewSession("test_id", user);
        sessionRegistry.getAllSessions(user, true);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("logout_token", "TOKKKKKENN");
        restTemplate.exchange(
                "http://localhost:" + port + "/backchannel_logout",
                HttpMethod.POST,
                new HttpEntity<>(body, headers),
                String.class
        );

        assertEquals(1, sessionRegistry.getAllSessions(user, true).size());
        assertEquals(true, sessionRegistry.getAllSessions(user, true).get(0).isExpired());
    }
}
