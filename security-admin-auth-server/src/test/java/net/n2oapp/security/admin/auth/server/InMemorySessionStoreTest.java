package net.n2oapp.security.admin.auth.server;

import net.n2oapp.security.admin.api.criteria.ClientCriteria;
import net.n2oapp.security.admin.api.model.Client;
import net.n2oapp.security.admin.api.provider.SsoUserRoleProvider;
import net.n2oapp.security.admin.api.service.ClientService;
import net.n2oapp.security.admin.impl.repository.AccountRepository;
import net.n2oapp.security.admin.impl.repository.RoleRepository;
import net.n2oapp.security.admin.impl.repository.UserRepository;
import org.apache.cxf.jaxrs.client.WebClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.access.event.AuthorizationFailureEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.context.request.RequestContextHolder;

import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(value = "classpath:test.properties", properties = {"spring.session.store-type=none"})
public class InMemorySessionStoreTest {

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private AccountRepository accountRepository;

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
    SessionRegistry sessionRegistry;

    @BeforeEach
    public void beforeEach() {
        host = "http://localhost:" + port;
        when(clientService.findByClientId("test")).thenReturn(client());

        when(clientService.findAll(Mockito.any(ClientCriteria.class))).thenReturn(
                new PageImpl<>(List.of(client()))
        );
    }

    @Test
    void inMemoryStore() throws IOException {
        Response response = WebClient.create(
                host + "/oauth/authorize?client_id=test&redirect_uri=https://localhost:8080/login&response_type=code&scope=read%20write&state=T45FVY"
        ).get();
        String session = (String) response.getMetadata().getFirst("Set-Cookie");
        session = session.split("JSESSIONID=")[1].split("; Path=/; HttpOnly")[0];

        SessionInformation storedSession = sessionRegistry.getSessionInformation(session);

        assertThat(storedSession.getSessionId(), is(session));
    }

    private static Client client() {
        Client client = new Client();
        client.setClientId("test");
        client.setIsAuthorizationCode(true);
        client.setIsClientGrant(true);
        client.setIsResourceOwnerPass(true);
        client.setClientSecret("test");
        client.setLogoutUrl("http://stubhostname.local");
        client.setRedirectUris("*");
        client.setEnabled(true);
        client.setAccessTokenValidityMinutes(10);
        client.setRefreshTokenValidityMinutes(10);
        return client;
    }

    @TestConfiguration
    static class HttpSessionConfig {

        @Bean
        public SessionRegistry sessionRegistry() {
            return new SessionRegistryImpl();
        }

        @Bean
        public ApplicationListener<AuthorizationFailureEvent> applicationListener() {
            return new ApplicationListener<>() {

                @Autowired
                private SessionRegistry sessionRegistry;

                @Override
                public void onApplicationEvent(AuthorizationFailureEvent event) {
                    String clientSessionId = RequestContextHolder.currentRequestAttributes().getSessionId();
                    sessionRegistry.registerNewSession(clientSessionId, event.getAuthentication());
                }
            };
        }
    }
}
