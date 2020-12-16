package net.n2oapp.security.admin.auth.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.model.Permission;
import net.n2oapp.security.admin.api.model.Role;
import net.n2oapp.security.admin.api.model.User;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.entity.PermissionEntity;
import net.n2oapp.security.admin.impl.entity.RoleEntity;
import net.n2oapp.security.admin.impl.entity.UserEntity;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestPropertySource("classpath:test.properties")
public class AuthProcessingTest {

    @LocalServerPort
    private String port;

    private String host;

    @MockBean
    private ClientService clientService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private SsoUserRoleProvider ssoUserRoleProvider;

    private static final RestTemplate client;

    static {
        client = new RestTemplate();
        HttpComponentsClientHttpRequestFactory f = new HttpComponentsClientHttpRequestFactory();
        f.setHttpClient(HttpClientBuilder.create().disableRedirectHandling().build());
        client.setRequestFactory(f);
    }

    @BeforeEach
    public void beforeEach() {
        host = "http://localhost:" + port;
        when(clientService.findByClientId("test")).thenReturn(client());

        when(clientService.findAll(Mockito.any(ClientCriteria.class))).thenReturn(
                new PageImpl<>(List.of(client()))
        );
        when(userRepository.findOneByUsernameIgnoreCase("testUser")).thenReturn(userEntity());
//        when(userDetailsService.loadUserDetails(Mockito.any(UserDetailsToken.class))).thenReturn(user());
    }

    @Test
    public void handleTest() {
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        NewCookie authSession = response.getCookies().get("JSESSIONID");
        response = WebClient.create(host + "/login/keycloak").cookie(authSession).get();
        String state = extractQueryParameter(response.getLocation(), "state");
        response = WebClient.create(
                host + "/login/keycloak" +
                        "?state=" + state + "&session_state=testSessionState" +
                        "&code=needError"
        ).cookie(authSession).get();
        response = WebClient.create(response.getLocation()).cookie(authSession).get();
        assertThat(response.getStatus(), is(HttpStatus.SC_UNAUTHORIZED));
        assertThat(response.getMediaType(), is(javax.ws.rs.core.MediaType.TEXT_HTML_TYPE));
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

        Map<String, Object> tokenResponse = client.exchange(host + "/oauth/token", HttpMethod.POST, entity, Map.class).getBody();
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
        assertThat((List<String>) claims.get("scope"), containsInAnyOrder("write", "read"));
    }

    /**
     * Тест получения токена по гранту refresh_token
     */
    @Test
    public void testRefreshToken() throws IOException {
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        assertThat(response.getStatus(), is(302));
        assertThat(response.getLocation().toString(), is(host + "/login/keycloak"));
        NewCookie authSession = response.getCookies().get("JSESSIONID");

        response = WebClient.create(host + "/login/keycloak").cookie(authSession).get();

        String state = extractQueryParameter(response.getLocation(), "state");

        response = WebClient.create(
                host + "/login/keycloak" +
                        "?state=" + state + "&session_state=testSessionState" +
                        "&code=testCode"
        ).cookie(authSession).get();
        response = WebClient.create(response.getLocation()).cookie(authSession).get();
        String code = extractQueryParameter(response.getLocation(), "code");

        Map<String, Object> tokenResponse = new ObjectMapper().readValue(
                WebClient.create(host + "/oauth/token")
                        .header("Authorization", "Basic dGVzdDp0ZXN0")
                        .form(new Form()
                                .param("grant_type", "authorization_code")
                                .param("code", code)
                                .param("redirect_uri", "https://localhost:8080/login")
                        ).readEntity(String.class),
                Map.class);

        assertThat(tokenResponse.get("access_token"), notNullValue());
        assertThat(tokenResponse.get("refresh_token"), notNullValue());
        assertThat(tokenResponse.get("token_type"), is("bearer"));
        assertThat(tokenResponse.get("expires_in"), notNullValue());
        assertThat(tokenResponse.get("scope"), notNullValue());
        assertThat(tokenResponse.get("jti"), notNullValue());

        String refreshToken = (String) tokenResponse.get("refresh_token");

        Map<String, Object> claims = new ObjectMapper().readValue(JwtHelper.decode(refreshToken).getClaims(), Map.class);
        assertThat(claims.get("sid"), notNullValue());
        assertThat(claims.get("roles"), notNullValue());
        assertThat(claims.get("permissions"), notNullValue());

        tokenResponse = new ObjectMapper().readValue(
                WebClient.create(host + "/oauth/token")
                        .header("Authorization", "Basic dGVzdDp0ZXN0")
                        .form(new Form()
                                .param("grant_type", "refresh_token")
                                .param("refresh_token", refreshToken)
                        ).readEntity(String.class),
                Map.class);

        assertThat(tokenResponse.get("access_token"), notNullValue());
        assertThat(tokenResponse.get("refresh_token"), notNullValue());
        assertThat(tokenResponse.get("token_type"), is("bearer"));
        assertThat(tokenResponse.get("expires_in"), notNullValue());
        assertThat(tokenResponse.get("scope"), notNullValue());
        assertThat(tokenResponse.get("jti"), notNullValue());

        String accessToken = (String) tokenResponse.get("access_token");

        claims = new ObjectMapper().readValue(JwtHelper.decode(accessToken).getClaims(), Map.class);
        assertThat(claims.get("sid"), notNullValue());

        refreshToken = (String) tokenResponse.get("refresh_token");

        claims = new ObjectMapper().readValue(JwtHelper.decode(refreshToken).getClaims(), Map.class);
        assertThat(claims.get("sid"), notNullValue());
    }

    @Test
    public void testAuthorizationCode() throws IOException {
        Cookie authSession = checkAuthentication();
        checkLogout(authSession);
    }

    private Cookie checkAuthentication() throws IOException {
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        assertThat(response.getStatus(), is(302));
        assertThat(response.getLocation().toString(), is(host + "/login/keycloak"));
        NewCookie authSession = response.getCookies().get("JSESSIONID");

        response = WebClient.create(host + "/login/keycloak").cookie(authSession).get();

        String state = extractQueryParameter(response.getLocation(), "state");

        response = WebClient.create(
                host + "/login/keycloak" +
                        "?state=" + state + "&session_state=testSessionState" +
                        "&code=testCode"
        ).cookie(authSession).get();
        response = WebClient.create(response.getLocation()).cookie(authSession).get();
        String code = extractQueryParameter(response.getLocation(), "code");

        Map<String, Object> tokenResponse = new ObjectMapper().readValue(
                WebClient.create(host + "/oauth/token")
                        .header("Authorization", "Basic dGVzdDp0ZXN0")
                        .form(new Form()
                                .param("grant_type", "authorization_code")
                                .param("code", code)
                                .param("redirect_uri", "https://localhost:8080/login")
                        ).readEntity(String.class),
                Map.class);

        assertThat(tokenResponse.get("access_token"), notNullValue());
        assertThat(tokenResponse.get("token_type"), is("bearer"));
        assertThat(tokenResponse.get("expires_in"), notNullValue());
        assertThat(tokenResponse.get("scope"), notNullValue());
        assertThat(tokenResponse.get("jti"), notNullValue());


        Map<String, Object> userInfo = new ObjectMapper().readValue(
                WebClient.create(host + "/userinfo")
                        .header("Authorization", "Bearer " + tokenResponse.get("access_token"))
                        .get()
                        .readEntity(String.class), Map.class);

        assertThat(userInfo.get("email"), is("testEmail"));
        assertThat(userInfo.get("username"), is("testUser"));
        assertThat(userInfo.get("name"), is("testName"));
        assertThat(userInfo.get("surname"), is("testSurname"));
        assertThat(userInfo.get("patronymic"), is("testPatronymic"));
        assertThat((List<String>) userInfo.get("roles"), hasItem("testRoleCode1"));
        assertThat((List<String>) userInfo.get("roles"), hasItem("testRoleCode2"));
        assertThat((List<String>) userInfo.get("permissions"), hasItem("testPermission1"));
        assertThat((List<String>) userInfo.get("permissions"), hasItem("testPermission2"));
        assertThat(userInfo.get("sid"), notNullValue());

        return authSession;
    }

    private void checkLogout(Cookie authCookie) {
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state="
                        + UUID.randomUUID().toString()
        ).cookie(authCookie).get();
        assertThat(extractQueryParameter(response.getLocation(), "code"), notNullValue());

        WebClient.create(host + "/logout?redirect_uri=http://localhost:8080/")
                .cookie(authCookie)
                .get();

        response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state="
                        + UUID.randomUUID().toString()
        ).cookie(authCookie).get();

        assertThat(extractQueryParameter(response.getLocation(), "code"), nullValue());
    }

    private static String extractQueryParameter(URI uri, String paramName) {
        return URLEncodedUtils.parse(uri, StandardCharsets.UTF_8).stream()
                .filter(param -> paramName.equals(param.getName()))
                .findFirst()
                .map(NameValuePair::getValue)
                .orElse(null);
    }

    private static Client client() {
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
        client.setLogoutUrl("http://stubhostname.local");
        return client;
    }

    private static User user() {
        User user = new User();
        user.setUsername("testUser");
        user.setEmail("testEmail");
        user.setName("testName");
        user.setSurname("testSurname");
        user.setPatronymic("testPatronymic");
        user.setRoles(Stream.of(1, 2).map(id -> {
            Role role = new Role();
            role.setId(id);
            role.setName("testRoleName" + id);
            role.setCode("testRoleCode" + id);
            Permission p = new Permission();
            p.setCode("testPermission" + id);
            role.setPermissions(List.of(p));
            return role;
        }).collect(Collectors.toList()));
        return user;
    }

    private static UserEntity userEntity() {
        UserEntity user = new UserEntity();
        user.setUsername("testUser");
        user.setEmail("testEmail");
        user.setName("testName");
        user.setSurname("testSurname");
        user.setPatronymic("testPatronymic");
        user.setRoleList(Stream.of(1, 2).map(id -> {
            RoleEntity role = new RoleEntity();
            role.setId(id);
            role.setName("testRoleName" + id);
            role.setCode("testRoleCode" + id);
            PermissionEntity p = new PermissionEntity();
            p.setCode("testPermission" + id);
            role.setPermissionList(List.of(p));
            return role;
        }).collect(Collectors.toList()));
        return user;
    }
}
