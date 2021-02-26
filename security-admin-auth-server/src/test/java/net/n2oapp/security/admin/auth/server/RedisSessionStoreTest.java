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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.PageImpl;
import org.springframework.session.Session;
import org.springframework.session.data.redis.RedisIndexedSessionRepository;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import redis.embedded.RedisServer;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:test.properties", properties = {"spring.session.store-type=redis"})
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

    @Autowired
    RedisIndexedSessionRepository redisRepository;

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
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        String session = (String) response.getMetadata().getFirst("Set-Cookie");
        session = session.split("SESSION=")[1].split("; Path=/; HttpOnly; SameSite=Lax")[0];

        Session repositorySession = redisRepository.findById(new String(Base64.getDecoder().decode(session.toString())));

        assertThat(repositorySession, notNullValue());
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

    @TestConfiguration
    public class TestRedisConfiguration {

        private RedisServer redisServer;

        public TestRedisConfiguration(RedisProperties redisProperties) {
            this.redisServer = new RedisServer(redisProperties.getPort());
        }

        @PostConstruct
        public void postConstruct() {
            redisServer.start();
        }

        @PreDestroy
        public void preDestroy() {
            redisServer.stop();
        }
    }
}
