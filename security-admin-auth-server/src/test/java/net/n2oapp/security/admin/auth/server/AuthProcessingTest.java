package net.n2oapp.security.admin.auth.server;


import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource("classpath:test.properties")
public class AuthProcessingTest {

    @LocalServerPort
    private String port;

    @MockBean
    private ClientService clientService;

    @MockBean
    private UserRepository userRepository;

    RestTemplate client = new RestTemplate();

    @BeforeEach
    public void beforeEach() {
        Mockito.when(clientService.findByClientId("test")).thenReturn(client());
    }


    /**
     * Тест получения токена по гранту client_credentials
     */
    @Test
    public void testClientCredentials() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Authorization", "Basic dGVzdDp0ZXN0");

        MultiValueMap<String, String> grantType = new LinkedMultiValueMap<>();
        grantType.add("grant_type", "client_credentials");
        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(grantType, headers);

        Map<String, Object> tokenResponse = client.exchange("http://localhost:" + port + "/oauth/token", HttpMethod.POST, entity, Map.class).getBody();
        assertThat(tokenResponse, notNullValue());
        assertThat(tokenResponse.get("token_type"), is("bearer"));
        assertThat(tokenResponse.get("jti"), notNullValue());
        assertThat((Integer) tokenResponse.get("expires_in"), greaterThan(1));
        assertThat((String) tokenResponse.get("scope"), containsString("read"));
        assertThat((String) tokenResponse.get("scope"), containsString("write"));

        assertThat(tokenResponse.get("access_token"), notNullValue());
        Map<String, Object> claims = new ObjectMapper().readValue(JwtHelper.decode((String) tokenResponse.get("access_token")).getClaims(), Map.class);
        assertThat(claims.get("jti"), is(tokenResponse.get("jti")));
        assertThat(claims.get("client_id"), is("test"));
        assertThat((List<String>) claims.get("scope"), contains("read", "write"));
    }

    private Client client() {
        Client client = new Client();
        client.setClientId("test");
        client.setClientSecret("test");
        client.setEnabled(true);
        client.setIsAuthorizationCode(true);
        client.setIsClientGrant(true);
        client.setIsResourceOwnerPass(true);
        client.setAccessTokenValidityMinutes(10);
        client.setRefreshTokenValidityMinutes(10);
        client.setRedirectUris("*");
        return client;
    }
}
