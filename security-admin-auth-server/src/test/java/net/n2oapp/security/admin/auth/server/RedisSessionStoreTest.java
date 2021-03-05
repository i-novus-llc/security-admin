package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:test.properties",
        properties = {"spring.session.store-type=redis",
                "spring.session.redis.configure-action=NONE"})
public class RedisSessionStoreTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private ClientService clientService;

    @MockBean
    private SsoUserRoleProvider ssoUserRoleProvider;

    @LocalServerPort
    private String port;

    private String host;

    @SpyBean
    JedisConnectionFactory jedisConnectionFactory;

    @BeforeEach
    public void beforeEach() {
        host = "http://localhost:" + port;
        when(clientService.findByClientId("test")).thenReturn(client());

        when(clientService.findAll(Mockito.any(ClientCriteria.class))).thenReturn(
                new PageImpl<>(List.of(client()))
        );
    }

    @Test
    void redisStore() throws IOException {
        WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        verify(jedisConnectionFactory, atLeastOnce()).getConnection();
    }

    private static Client client() {
        Client client = new Client();
        client.setClientSecret("test");
        client.setClientId("test");
        client.setIsAuthorizationCode(true);
        client.setEnabled(true);
        client.setIsResourceOwnerPass(true);
        client.setIsClientGrant(true);
        client.setRefreshTokenValidityMinutes(10);
        client.setAccessTokenValidityMinutes(10);
        client.setLogoutUrl("http://stubhostname.local");
        client.setRedirectUris("*");
        return client;
    }
}
